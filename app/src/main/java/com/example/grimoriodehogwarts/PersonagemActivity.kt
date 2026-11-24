package com.example.grimoriodehogwarts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.grimoriodehogwarts.api.HogwartsApi
import com.example.grimoriodehogwarts.model.Personagem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PersonagemActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var btnBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensagem: TextView
    private lateinit var layoutResultado: LinearLayout
    private lateinit var tvQuantidade: TextView
    private lateinit var layoutLista: LinearLayout
    private lateinit var hogwartsApi: HogwartsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personagem)
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
        tvQuantidade = findViewById(R.id.tvQuantidade)
        layoutLista = findViewById(R.id.layoutLista)

        // Configura Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://hogwarts-api.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        hogwartsApi = retrofit.create(HogwartsApi::class.java)
    } //Fim onCreate

    // Chamado pelo botão Buscar: valida a entrada antes de consultar a API
    fun buscarPersonagem(view: View) {
        val nome = etNome.text.toString().trim()

        if (nome.isEmpty()) {
            etNome.error = getString(R.string.erro_campo_vazio)
        } else if (nome.length < 3) {
            etNome.error = getString(R.string.erro_minimo_caracteres)
        } else {
            buscarPersonagemApi(nome)
        }
    }

    // Função para buscar o personagem usando corrotinas
    private fun buscarPersonagemApi(nome: String) {
        lifecycleScope.launch {
            try {
                showProgressBar()
                // Faz a requisição em segundo plano
                val response = withContext(Dispatchers.IO) {
                    hogwartsApi.buscarPersonagens(nome)
                }

                // Um mesmo trecho pode retornar vários personagens: exibe todos
                val personagens = response.data

                if (personagens.isNotEmpty()) {
                    mostrarPersonagens(personagens)
                } else {
                    mostrarMensagem(getString(R.string.msg_nenhum_personagem))
                }
            } catch (e: Exception) {
                Log.e("PersonagemActivity", "Erro ao buscar o personagem", e)
                mostrarMensagem(getString(R.string.msg_erro_conexao))
            } finally {
                hideProgressBar()
            }
        } //Fim corrotina
    } //Fim buscarPersonagemApi

    // Cria um cartão (item_personagem.xml) para cada personagem encontrado
    private fun mostrarPersonagens(personagens: List<Personagem>) {
        layoutLista.removeAllViews()

        for (personagem in personagens) {
            val item = layoutInflater.inflate(R.layout.item_personagem, layoutLista, false)
            item.findViewById<TextView>(R.id.tvNome).text =
                getString(R.string.label_nome, valorOuPadrao(personagem.nome))
            item.findViewById<TextView>(R.id.tvEspecie).text =
                getString(R.string.label_especie, valorOuPadrao(personagem.especie))
            item.findViewById<TextView>(R.id.tvCasa).text =
                getString(R.string.label_casa, valorOuPadrao(personagem.casa))
            layoutLista.addView(item)
        }

        tvQuantidade.text = resources.getQuantityString(
            R.plurals.personagens_encontrados, personagens.size, personagens.size
        )
        layoutResultado.visibility = View.VISIBLE
    }

    // Volta para o menu inicial
    fun voltarAoMenu(view: View) {
        finish()
    }

    // Dado ausente na API vira "Não informado"
    private fun valorOuPadrao(valor: String?): String {
        return if (valor.isNullOrBlank()) getString(R.string.nao_informado) else valor
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
