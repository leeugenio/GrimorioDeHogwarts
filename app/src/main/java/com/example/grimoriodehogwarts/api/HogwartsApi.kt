package com.example.grimoriodehogwarts.api

import com.example.grimoriodehogwarts.model.Criatura
import com.example.grimoriodehogwarts.model.HogwartsResponse
import com.example.grimoriodehogwarts.model.Personagem
import retrofit2.http.GET
import retrofit2.http.Query

interface HogwartsApi {

    // Ex.: GET https://hogwarts-api.com/api/characters?search=Weasley
    @GET("characters")
    suspend fun buscarPersonagens(@Query("search") nome: String): HogwartsResponse<Personagem>

    // Ex.: GET https://hogwarts-api.com/api/creatures?search=Hippogriff
    @GET("creatures")
    suspend fun buscarCriaturas(@Query("search") nome: String): HogwartsResponse<Criatura>
}
