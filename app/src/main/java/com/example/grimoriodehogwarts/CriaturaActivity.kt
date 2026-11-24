package com.example.grimoriodehogwarts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.grimoriodehogwarts.api.HogwartsApi
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CriaturaActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var btnBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensagem: TextView
    private lateinit var layoutResultado: LinearLayout
    private lateinit var tvNome: TextView
    private lateinit var ivCriatura: ImageView
    private lateinit var tvImagemIndisponivel: TextView
    private lateinit var hogwartsApi: HogwartsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_criatura)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNome = findViewById(R.id.etNome)
        btnBuscar = findViewById(R.id.btnBuscar)
        progressBar = findViewById(R.id.progressBar)
        tvMensagem = findViewById(R.id.tvMensagem)
        layoutResultado = findViewById(R.id.layoutResultado)
        tvNome = findViewById(R.id.tvNome)
        ivCriatura = findViewById(R.id.ivCriatura)
        tvImagemIndisponivel = findViewById(R.id.tvImagemIndisponivel)

        // Configura Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://hogwarts-api.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        hogwartsApi = retrofit.create(HogwartsApi::class.java)
    } //Fim onCreate

    // Chamado pelo botão Buscar: valida a entrada antes de consultar a API
    fun buscarCriatura(view: View) {
        val nome = etNome.text.toString().trim()

        if (nome.isEmpty()) {
            etNome.error = getString(R.string.erro_campo_vazio)
        } else if (nome.length < 3) {
            etNome.error = getString(R.string.erro_minimo_caracteres)
        } else {
            buscarCriaturaApi(nome)
        }
    }

    // Função para buscar a criatura usando corrotinas
    private fun buscarCriaturaApi(nome: String) {
        lifecycleScope.launch {
            try {
                showProgressBar()
                // Faz a requisição em segundo plano
                val response = withContext(Dispatchers.IO) {
                    hogwartsApi.buscarCriaturas(nome)
                }

                // Dá preferência à criatura com o nome exato; senão usa a primeira
                val criatura = response.data.firstOrNull { it.nome.equals(nome, ignoreCase = true) }
                    ?: response.data.firstOrNull()

                if (criatura != null) {
                    tvNome.text = criatura.nome ?: getString(R.string.nao_informado)
                    // O ProgressBar só é escondido quando a imagem terminar de carregar
                    carregarImagem(criatura.imagem)
                } else {
                    hideProgressBar()
                    mostrarMensagem(getString(R.string.msg_nenhuma_criatura))
                }
            } catch (e: Exception) {
                Log.e("CriaturaActivity", "Erro ao buscar a criatura", e)
                hideProgressBar()
                mostrarMensagem(getString(R.string.msg_erro_conexao))
            }
        } //Fim corrotina
    } //Fim buscarCriaturaApi

    // O Picasso baixa a imagem em segundo plano, fora da thread principal
    private fun carregarImagem(url: String?) {
        if (url.isNullOrBlank()) {
            mostrarImagemIndisponivel()
            return
        }

        Picasso.get()
            .load(url)
            .resize(1024, 0) // Limita o tamanho mantendo a proporção da imagem
            .onlyScaleDown()
            .into(ivCriatura, object : Callback {
                override fun onSuccess() {
                    hideProgressBar()
                    tvImagemIndisponivel.visibility = View.GONE
                    layoutResultado.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    Log.e("CriaturaActivity", "Erro ao carregar a imagem", e)
                    mostrarImagemIndisponivel()
                }
            })
    }

    // Sem imagem ou falha no carregamento: usa a imagem substituta do app
    private fun mostrarImagemIndisponivel() {
        hideProgressBar()
        ivCriatura.setImageResource(R.drawable.ic_imagem_indisponivel)
        tvImagemIndisponivel.visibility = View.VISIBLE
        layoutResultado.visibility = View.VISIBLE
    }

    private fun mostrarMensagem(mensagem: String) {
        tvMensagem.text = mensagem
        tvMensagem.visibility = View.VISIBLE
    }

    // Função para mostrar o ProgressBar
    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        btnBuscar.isEnabled = false
        tvMensagem.visibility = View.GONE
        layoutResultado.visibility = View.GONE
    }

    // Função para esconder o ProgressBar
    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        btnBuscar.isEnabled = true
    }
}
