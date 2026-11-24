
package com.example.grimoriodehogwarts.api

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

object HogwartsApi {

    private const val BASE_URL = "https://hogwarts-api.com/api"

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun buscarPersonagens(
        nome: String,
        callback: (Result<JSONArray>) -> Unit
    ) {
        buscar("characters", nome, callback)
    }

    fun buscarCriaturas(
        nome: String,
        callback: (Result<JSONArray>) -> Unit
    ) {
        buscar("creatures", nome, callback)
    }

    private fun buscar(
        endpoint: String,
        nome: String,
        callback: (Result<JSONArray>) -> Unit
    ) {
        executor.execute {
            val resultado = runCatching {
                val busca = URLEncoder.encode(nome, "UTF-8")
                val url = URL("$BASE_URL/$endpoint?search=$busca")

                val conexao = url.openConnection() as HttpURLConnection

                try {
                    conexao.requestMethod = "GET"
                    conexao.connectTimeout = 10000
                    conexao.readTimeout = 10000

                    val codigo = conexao.responseCode

                    if (codigo != HttpURLConnection.HTTP_OK) {
                        throw Exception("Erro ao consultar o servidor: $codigo")
                    }

                    val resposta = conexao.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                    JSONObject(resposta).getJSONArray("data")

                } finally {
                    conexao.disconnect()
                }
            }

            mainHandler.post {
                callback(resultado)
            }
        }
    }
}
