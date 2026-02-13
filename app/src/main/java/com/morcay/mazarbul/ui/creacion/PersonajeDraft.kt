package com.morcay.mazarbul.ui.creacion

data class PersonajeDraft(
    val nombre: String,
    val trasfondo: String,
    val raza: String,
    val subraza: String,
    val atributos: Map<String, Int>,
    val clase: String,
    val subclase: String,
    val habilidades: List<String>
)
