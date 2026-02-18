package com.morcay.mazarbul.rules

import java.text.Normalizer

/**
 * Reglas PF2e:
 * - Subrazas por raza
 * - Subclases por clase
 *
 * ✅ Versión robusta: compara sin tildes y sin mayúsculas.
 */
object SubopcionesPF2 {

    // -------------------------
    // Normalización (clave)
    // -------------------------
    private fun keyOf(texto: String): String {
        val sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "") // quita diacríticos
        return sinTildes.lowercase()
    }

    // =========================
    // SUBRAZAS POR RAZA
    // =========================

    private val subrazasPorRaza: Map<String, List<String>> = mapOf(
        keyOf("Enano") to listOf("Enano de las montañas", "Enano de las colinas", "Enano gris"),
        keyOf("Elfo") to listOf("Alto elfo", "Elfo de los bosques", "Elfo oscuro"),
        keyOf("Gnomo") to listOf("Gnomo de las profundidades", "Gnomo feérico"),
        keyOf("Humano") to listOf("Humano versátil", "Humano adaptado"),
        keyOf("Mediano") to listOf("Mediano piesligeros", "Mediano fornido")
    )

    fun subrazasDe(raza: String?): List<String> {
        if (raza.isNullOrBlank()) return emptyList()
        return subrazasPorRaza[keyOf(raza)] ?: emptyList()
    }

    // =========================
    // SUBCLASES POR CLASE
    // =========================

    private val subclasesPorClase: Map<String, List<String>> = mapOf(
        keyOf("Alquimista") to listOf("Bombero", "Mutágeno", "Toxicólogo"),
        keyOf("Barbaro") to listOf("Instinto animal", "Instinto furioso", "Instinto dracónico"),
        keyOf("Bardo") to listOf("Musa del valor", "Musa del conocimiento"),
        keyOf("Campeon") to listOf("Paladín", "Redentor", "Liberador"),
        keyOf("Clerigo") to listOf("Doctrina de guerra", "Doctrina de sanación"),
        keyOf("Druida") to listOf("Orden de la hoja", "Orden de la tormenta"),
        keyOf("Explorador") to listOf("Cazador", "Acechador"),
        keyOf("Guerrero") to listOf("Maestro de armas", "Defensor", "Táctico"),
        keyOf("Hechicero") to listOf("Linaje dracónico", "Linaje feérico"),
        keyOf("Mago") to listOf("Evocador", "Ilusionista", "Nigromante"),
        keyOf("Monje") to listOf("Puño abierto", "Camino de la montaña"),
        keyOf("Picaro") to listOf("Ladrón", "Embaucador")
    )

    fun subclasesDe(clase: String?): List<String> {
        if (clase.isNullOrBlank()) return emptyList()
        return subclasesPorClase[keyOf(clase)] ?: emptyList()
    }
}
