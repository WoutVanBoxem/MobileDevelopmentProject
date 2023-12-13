package com.example.mobiledevelopmentproject

import java.io.Serializable

data class Match (
    val id: String = "",
    val datum: String = "",
    var deelnemers: List<String> = listOf(),
    val isPubliek: Boolean = false,
    val tijdslotId: String = "",
    val veldNaam: String = ""
) : Serializable