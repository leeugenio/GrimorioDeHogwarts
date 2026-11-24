package com.example.grimoriodehogwarts.model

import com.google.gson.annotations.SerializedName

// Campos opcionais: algumas criaturas não possuem imagem
data class Criatura(
    @SerializedName("name")
    val nome: String?,
    @SerializedName("image")
    val imagem: String?
)
