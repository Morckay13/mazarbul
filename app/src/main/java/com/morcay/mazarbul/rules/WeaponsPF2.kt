package com.morcay.mazarbul.rules

data class WeaponBase(
    val nombre: String,
    val dano: String, // "1d8 cortante", etc.
    val esFinesse: Boolean = false // opcional futuro
)

object WeaponsPF2 {
    val armas = listOf(
        WeaponBase("Sin arma", "—"),
        WeaponBase("Espada larga", "1d8 cortante"),
        WeaponBase("Daga", "1d4 perforante", esFinesse = true),
        WeaponBase("Bastón", "1d6 contundente"),
        WeaponBase("Arco corto", "1d6 perforante")
    )

    fun fromNombre(nombre: String?): WeaponBase {
        val n = (nombre ?: "").trim()
        return armas.firstOrNull { it.nombre == n } ?: armas.first()
    }
}