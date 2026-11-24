
package com.example.grimoriodehogwarts

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.grimoriodehogwarts.api.HogwartsApi
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class CriaturaActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var btnBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtMensagem: TextView
    private lateinit var layoutResultado: LinearLayout
    private lateinit var txtNome: TextView
    private lateinit var imgCriatura: ImageView
    private lateinit var txtImagemIndisponivel: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criatura)

        // Referências dos componentes da tela
        edtNome = findViewById(R.id.edtNomeCriatura)
        btnBuscar = findViewById(R.id.btnBuscarCriatura)
        progressBar = findViewById(R.id.progressCriatura)
        txtMensagem = findViewById(R.id.txtMensagemCriatura)
        layoutResultado = findViewById(R.id.layoutResultadoCriatura)
        txtNome = findViewById(R.id.txtNomeCriatura)
        imgCriatura = findViewById(R.id.imgCriatura)
        txtImagemIndisponivel =
            findViewById(R.id.txtImagemIndisponivel)

        // Ação do botão Buscar
        btnBuscar.setOnClickListener {

            val nome = edtNome.text.toString().trim()

            // Validação do campo vazio
            if (nome.isEmpty()) {
                edtNome.error = "Digite o nome da criatura"
                return@setOnClickListener
            }

            // Validação mínima de 3 caracteres
            if (nome.length < 3) {
                edtNome.error = "Digite pelo menos 3 caracteres"
                return@setOnClickListener
            }

            // Preparando a tela
            edtNome.error = null
            txtMensagem.visibility = View.GONE
            layoutResultado.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            btnBuscar.isEnabled = false

            // Consultando a API
            HogwartsApi.buscarCriaturas(nome) { resultado ->

                resultado.onSuccess { criaturas ->

                    if (criaturas.length() == 0) {

                        progressBar.visibility = View.GONE
                        btnBuscar.isEnabled = true

                        txtMensagem.text =
                            "Nenhuma criatura encontrada"

                        txtMensagem.visibility = View.VISIBLE

                    } else {

                        val criatura = criaturas.getJSONObject(0)

                        txtNome.text =
                            obterCampo(criatura, "name")

                        layoutResultado.visibility = View.VISIBLE

                        val imagemUrl =
                            obterCampo(criatura, "image")

                        if (imagemUrl == "Não informado") {

                            mostrarImagemIndisponivel()

                        } else {

                            carregarImagem(imagemUrl)
                        }
                    }
                }

                resultado.onFailure {

                    progressBar.visibility = View.GONE
                    btnBuscar.isEnabled = true

                    txtMensagem.text =
                        "Erro ao consultar. Verifique sua internet e tente novamente."

                    txtMensagem.visibility = View.VISIBLE
                }
            }
        }
    }

    // Baixa a imagem em segundo plano
    private fun carregarImagem(urlImagem: String) {

        imgCriatura.visibility = View.GONE
        txtImagemIndisponivel.visibility = View.GONE

        executor.execute {

            val imagem = runCatching {

                val conexao =
                    URL(urlImagem).openConnection() as HttpURLConnection

                try {
                    conexao.connectTimeout = 10000
                    conexao.readTimeout = 10000
                    conexao.requestMethod = "GET"

                    if (conexao.responseCode != HttpURLConnection.HTTP_OK) {
                        throw Exception("Não foi possível carregar a imagem")
                    }

                    conexao.inputStream.use {
                        BitmapFactory.decodeStream(it)
                            ?: throw Exception("Imagem inválida")
                    }

                } finally {
                    conexao.disconnect()
                }
            }

            mainHandler.post {

                progressBar.visibility = View.GONE
                btnBuscar.isEnabled = true

                imagem.onSuccess { bitmap ->

                    imgCriatura.setImageBitmap(bitmap)
                    imgCriatura.visibility = View.VISIBLE
                    txtImagemIndisponivel.visibility = View.GONE

                }.onFailure {

                    mostrarImagemIndisponivel()
                }
            }
        }
    }

    // Exibe a mensagem quando a imagem não está disponível
    private fun mostrarImagemIndisponivel() {

        progressBar.visibility = View.GONE
        btnBuscar.isEnabled = true

        imgCriatura.setImageDrawable(null)
        imgCriatura.visibility = View.GONE

        txtImagemIndisponivel.visibility = View.VISIBLE
    }

    // Trata informações ausentes na API
    private fun obterCampo(
        objeto: JSONObject,
        campo: String
    ): String {

        val valor = objeto.opt(campo)

        return if (
            valor == null ||
            valor == JSONObject.NULL ||
            valor.toString().isBlank() ||
            valor.toString() == "null"
        ) {
            "Não informado"
        } else {
            valor.toString()
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
