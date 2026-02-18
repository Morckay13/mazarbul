package com.morcay.mazarbul.rules

/**
 * Catálogo oficial (v1) de reglas mínimas PF2e para tu app.
 * Aquí centralizamos datos para evitar "contains()" y tener consistencia.
 */

enum class Ascendencia(
    val nombre: String,
    val pgBase: Int,
    val velocidad: Int
) {
    ELFO("Elfo", 6, 30),
    ENANO("Enano", 10, 20),
    GNOMO("Gnomo", 8, 25),
    HUMANO("Humano", 8, 25),
    MEDIANO("Mediano", 6, 25);

    companion object {
        /**
         * Devuelve la ascendencia a partir del texto guardado en BD.
         * - Si no encuentra match exacto, devuelve null (y usaremos fallback).
         */
        fun fromNombre(nombre: String?): Ascendencia? {
            if (nombre.isNullOrBlank()) return null
            return entries.firstOrNull { it.nombre.equals(nombre.trim(), ignoreCase = true) }
        }
    }
}

enum class ClasePF2(
    val nombre: String,
    val pgPorNivel: Int
) {
    ALQUIMISTA("Alquimista", 8),
    BARBARO("Barbaro", 12),
    BARDO("Bardo", 8),
    CAMPEON("Campeon", 10),
    CLERIGO("Clerigo", 8),
    DRUIDA("Druida", 8),
    EXPLORADOR("Explorador", 10),
    GUERRERO("Guerrero", 10),
    HECHICERO("Hechicero", 6),
    MAGO("Mago", 6),
    MONJE("Monje", 10),
    PICARO("Picaro", 8);

    companion object {
        fun fromNombre(nombre: String?): ClasePF2? {
            if (nombre.isNullOrBlank()) return null
            return entries.firstOrNull { it.nombre.equals(nombre.trim(), ignoreCase = true) }
        }
    }
}
