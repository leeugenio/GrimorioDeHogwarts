package com.example.grimoriodehogwarts.model

// A Hogwarts API devolve a lista de resultados dentro do campo "data"
data class HogwartsResponse<T>(
    val data: List<T>
)
