package com.example.grimoriodehogwarts.model

import com.google.gson.annotations.SerializedName

// Campos opcionais: a API pode não informar espécie ou casa
data class Personagem(
    @SerializedName("name")
    val nome: String?,
    @SerializedName("species")
    val especie: String?,
    @SerializedName("house")
    val casa: String?
)
