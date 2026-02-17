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
    val atributos: String,
    val clase: String,
    val subclase: String,
    val habilidades: String,
    val nivel: Int
)

