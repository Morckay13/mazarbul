package com.morcay.mazarbul.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personajes")
data class PersonajeEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val trasfondo: String,

    val raza: String,
    val subraza: String,

    // Atributos serializados como texto: Fuerza:15;Destreza:14...
    val atributos: String,

    val clase: String,
    val subclase: String,

    // Habilidades separadas por coma
    val habilidades: String
)
