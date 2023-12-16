package com.example.mobiledevelopmentproject

import java.io.Serializable

data class Match (
    var id: String = "",
    val datum: String = "",
    var deelnemers: List<String> = listOf(),
    val isPubliek: Boolean = true,
    val tijdslotId: String = "",
    val veldNaam: String = "",
    var clubNaam: String = "",
    var clubAdres: String = ""
) : Serializable