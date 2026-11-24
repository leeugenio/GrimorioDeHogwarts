
package com.example.grimoriodehogwarts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.grimoriodehogwarts.api.HogwartsApi
import org.json.JSONObject

class PersonagemActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var btnBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtMensagem: TextView
    private lateinit var layoutResultado: LinearLayout

    private lateinit var txtNome: TextView
    private lateinit var txtEspecie: TextView
    private lateinit var txtCasa: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personagem)

        // Referências dos componentes da tela
        edtNome = findViewById(R.id.edtNomePersonagem)
        btnBuscar = findViewById(R.id.btnBuscarPersonagem)
        progressBar = findViewById(R.id.progressPersonagem)
        txtMensagem = findViewById(R.id.txtMensagemPersonagem)
        layoutResultado = findViewById(R.id.layoutResultadoPersonagem)

        txtNome = findViewById(R.id.txtNomePersonagem)
        txtEspecie = findViewById(R.id.txtEspeciePersonagem)
        txtCasa = findViewById(R.id.txtCasaPersonagem)

        // Ação do botão Buscar
        btnBuscar.setOnClickListener {

            val nome = edtNome.text.toString().trim()

            // Validação do campo vazio
            if (nome.isEmpty()) {
                edtNome.error = "Digite o nome do personagem"
                return@setOnClickListener
            }

            // Validação de quantidade mínima de caracteres
            if (nome.length < 3) {
                edtNome.error = "Digite pelo menos 3 caracteres"
                return@setOnClickListener
            }

            // Preparando a tela para a consulta
            edtNome.error = null
            txtMensagem.visibility = View.GONE
            layoutResultado.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            btnBuscar.isEnabled = false

            // Consultando a API
            HogwartsApi.buscarPersonagens(nome) { resultado ->

                progressBar.visibility = View.GONE
                btnBuscar.isEnabled = true

                resultado.onSuccess { personagens ->

                    val personagem = (0 until personagens.length())
                        .mapNotNull { personagens.optJSONObject(it) }
                        .firstOrNull { item ->
                            val nomeApi = item.optString("name", "").lowercase()
                            nome.lowercase()
                                .split(Regex("\\s+"))
                                .all { termo -> nomeApi.contains(termo) }
                        }

                    if (personagem == null) {
                        txtMensagem.text = "Nenhum personagem encontrado"
                        txtMensagem.visibility = View.VISIBLE
                    } else {
                        txtNome.text = "Nome: " + obterCampo(personagem, "name")
                        txtEspecie.text = "Espécie: " + obterCampo(personagem, "species")
                        txtCasa.text = "Casa: " + obterCampo(personagem, "house")
                        layoutResultado.visibility = View.VISIBLE
                    }
                }

                resultado.onFailure {

                    txtMensagem.text =
                        "Erro ao consultar. Verifique sua internet e tente novamente."

                    txtMensagem.visibility = View.VISIBLE
                }
            }
        }
    }

    // Trata informações que não existem na API
    private fun obterCampo(
        objeto: JSONObject,
        campo: String
    ): String {

        val valor = objeto.opt(campo)

        return if (
            valor == null ||
            valor == JSONObject.NULL ||
            valor.toString().isBlank()
        ) {
            "Não informado"
        } else {
            valor.toString()
        }
    }
}
