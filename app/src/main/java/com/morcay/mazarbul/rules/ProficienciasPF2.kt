package com.morcay.mazarbul.rules

/**
 * Grados de competencia PF2e.
 * U = Untrained, T = Trained, E = Expert, M = Master, L = Legendary
 */
enum class Grado { U, T, E, M, L }

/** Bonus de competencia PF2e: grado + nivel (salvo Untrained). */
fun bonusCompetencia(grado: Grado, nivel: Int): Int = when (grado) {
    Grado.U -> 0
    Grado.T -> nivel + 2
    Grado.E -> nivel + 4
    Grado.M -> nivel + 6
    Grado.L -> nivel + 8
}

/**
 * Proficiencias por clase (PF2e).
 * Implementadas:
 * - CAMPEÓN (real)
 * - GUERRERO (placeholder razonable)
 * - MONJE (real, con nota de Path to Perfection)
 * - CLÉRIGO (real, doctrina afecta armaduras)
 * - MAGO (real)
 * - HECHICERO (real)
 * - DRUIDA (realista y coherente con PF2e; afinable por remaster si quieres)
 * - BARDO (realista y coherente con PF2e; will muy fuerte)
 * - ALQUIMISTA (realista y coherente con PF2e; afinable)
 */
object ProficienciasPF2 {

    // ------------------------------------------------------------
    // Normalización
    // ------------------------------------------------------------
    private fun norm(s: String?): String = (s ?: "")
        .trim()
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")

    private fun es(clase: String?, nombre: String): Boolean {
        val c = norm(clase)
        return c == nombre || c.contains(nombre)
    }

    private fun esSub(subclase: String?, token: String): Boolean {
        val s = norm(subclase)
        return s == token || s.contains(token)
    }

    fun esCampeon(clase: String?) = es(clase, "campeon")
    fun esGuerrero(clase: String?) = es(clase, "guerrero")
    fun esMonje(clase: String?) = es(clase, "monje")
    fun esClerigo(clase: String?) = es(clase, "clerigo")
    fun esMago(clase: String?) = es(clase, "mago") || es(clase, "wizard")
    fun esHechicero(clase: String?) = es(clase, "hechicero") || es(clase, "sorcerer")

    fun esDruida(clase: String?) = es(clase, "druida") || es(clase, "druid")
    fun esBardo(clase: String?) = es(clase, "bardo") || es(clase, "bard")
    fun esAlquimista(clase: String?) = es(clase, "alquimista") || es(clase, "alchemist")

    // ------------------------------------------------------------
    // DEFENSA (CA): grado por clase + nivel + categoría de armadura
    // ------------------------------------------------------------
    fun gradoDefensa(
        clase: String?,
        nivel: Int,
        categoria: ArmorCategory,
        subclase: String? = null
    ): Grado {

        // ✅ CAMPEÓN (real)
        if (esCampeon(clase)) {
            return when {
                nivel >= 17 -> Grado.L
                nivel >= 13 -> Grado.M
                nivel >= 7  -> Grado.E
                else        -> Grado.T
            }
        }

        // ✅ GUERRERO (placeholder razonable)
        if (esGuerrero(clase)) {
            return when {
                nivel >= 13 -> Grado.M
                nivel >= 11 -> Grado.E
                else -> Grado.T
            }
        }

        // ✅ MONJE (real)
        if (esMonje(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED -> when {
                    nivel >= 17 -> Grado.L
                    nivel >= 13 -> Grado.M
                    else -> Grado.E
                }
                ArmorCategory.LIGHT,
                ArmorCategory.MEDIUM,
                ArmorCategory.HEAVY -> Grado.U
            }
        }

        // ✅ CLÉRIGO (real + doctrina)
        if (esClerigo(clase)) {
            if (categoria == ArmorCategory.UNARMORED) {
                // clérigo mejora su defensa “sin armadura” con el tiempo
                return if (nivel >= 13) Grado.E else Grado.T
            }

            val esGuerra = esSub(subclase, "doctrina de guerra") || esSub(subclase, "guerra")
            val esSanacion = esSub(subclase, "doctrina de sanacion") || esSub(subclase, "sanacion")

            return if (esGuerra) {
                when (categoria) {
                    ArmorCategory.LIGHT, ArmorCategory.MEDIUM -> Grado.T
                    ArmorCategory.HEAVY -> Grado.U
                    else -> Grado.T
                }
            } else if (esSanacion) {
                Grado.U
            } else {
                Grado.U
            }
        }

        // ✅ MAGO (real)
        if (esMago(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED -> Grado.T
                ArmorCategory.LIGHT,
                ArmorCategory.MEDIUM,
                ArmorCategory.HEAVY -> Grado.U
            }
        }

        // ✅ HECHICERO (real)
        if (esHechicero(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED -> Grado.T
                ArmorCategory.LIGHT,
                ArmorCategory.MEDIUM,
                ArmorCategory.HEAVY -> Grado.U
            }
        }

        // ✅ DRUIDA (realista PF2e)
        // En general: entrenado en unarmored + light + medium; no heavy.
        // Y mejora “medium armor” más tarde (lo aplicamos como mejora general a E en 13).
        if (esDruida(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED,
                ArmorCategory.LIGHT,
                ArmorCategory.MEDIUM -> if (nivel >= 13) Grado.E else Grado.T
                ArmorCategory.HEAVY -> Grado.U
            }
        }

        // ✅ BARDO (realista PF2e)
        // Unarmored + Light entrenado, mejora a experto más tarde (lo aplicamos en 13).
        if (esBardo(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED,
                ArmorCategory.LIGHT -> if (nivel >= 13) Grado.E else Grado.T
                ArmorCategory.MEDIUM,
                ArmorCategory.HEAVY -> Grado.U
            }
        }

        // ✅ ALQUIMISTA (realista PF2e)
        // Unarmored + Light entrenado, mejora a experto más tarde (lo aplicamos en 13).
        if (esAlquimista(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED,
                ArmorCategory.LIGHT -> if (nivel >= 13) Grado.E else Grado.T
                ArmorCategory.MEDIUM,
                ArmorCategory.HEAVY -> Grado.U
            }
        }

        // Fallback temporal
        return when (categoria) {
            ArmorCategory.UNARMORED -> Grado.T
            else -> Grado.U
        }
    }

    // ------------------------------------------------------------
    // PERCEPCIÓN: grado por clase + nivel
    // ------------------------------------------------------------
    fun gradoPercepcion(clase: String?, nivel: Int): Grado {

        if (esCampeon(clase)) return if (nivel >= 11) Grado.E else Grado.T
        if (esMonje(clase)) return if (nivel >= 5) Grado.E else Grado.T
        if (esClerigo(clase)) return if (nivel >= 5) Grado.E else Grado.T

        if (esMago(clase)) return if (nivel >= 11) Grado.E else Grado.T
        if (esHechicero(clase)) return if (nivel >= 11) Grado.E else Grado.T

        // ✅ DRUIDA (realista)
        return when {
            esDruida(clase) -> when {
                nivel >= 15 -> Grado.M
                nivel >= 7  -> Grado.E
                else        -> Grado.T
            }
            // ✅ BARDO (realista: percepción muy buena, llega a M)
            esBardo(clase) -> when {
                nivel >= 11 -> Grado.M
                nivel >= 5  -> Grado.E
                else        -> Grado.T
            }
            // ✅ ALQUIMISTA (realista)
            esAlquimista(clase) -> when {
                nivel >= 15 -> Grado.M
                nivel >= 7  -> Grado.E
                else        -> Grado.T
            }
            // Guerrero placeholder
            esGuerrero(clase) -> if (nivel >= 7) Grado.E else Grado.T
            else -> Grado.T
        }
    }

    // ------------------------------------------------------------
    // TS: grados por clase + nivel
    // ------------------------------------------------------------
    fun gradosTS(clase: String?, nivel: Int): Triple<Grado, Grado, Grado> {

        // ✅ CAMPEÓN (real)
        if (esCampeon(clase)) {
            val fort = if (nivel >= 9) Grado.M else Grado.E
            val ref  = if (nivel >= 9) Grado.E else Grado.T
            val vol  = if (nivel >= 11) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // ✅ MONJE (real, con defaults)
        if (esMonje(clase)) {
            var fort = Grado.E
            var ref = Grado.E
            var vol = Grado.E

            if (nivel >= 7) ref = Grado.M
            if (nivel >= 11) vol = Grado.M
            if (nivel >= 15) ref = Grado.L

            return Triple(fort, ref, vol)
        }

        // ✅ CLÉRIGO (realista)
        if (esClerigo(clase)) {
            val fort = Grado.T
            val ref = if (nivel >= 11) Grado.E else Grado.T
            val vol = if (nivel >= 9) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // ✅ MAGO (realista)
        if (esMago(clase)) {
            val fort = Grado.T
            val ref = if (nivel >= 15) Grado.M else Grado.E
            val vol = if (nivel >= 9) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // ✅ HECHICERO (realista)
        if (esHechicero(clase)) {
            val fort = Grado.T
            val ref = if (nivel >= 15) Grado.M else Grado.E
            val vol = if (nivel >= 9) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // ✅ DRUIDA (realista PF2e)
        if (esDruida(clase)) {
            val fort = if (nivel >= 11) Grado.E else Grado.T
            val ref  = if (nivel >= 11) Grado.E else Grado.T
            val vol  = if (nivel >= 9) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // ✅ BARDO (realista PF2e)
        if (esBardo(clase)) {
            val fort = if (nivel >= 9) Grado.E else Grado.T
            val ref  = if (nivel >= 3) Grado.E else Grado.T
            val vol  = when {
                nivel >= 17 -> Grado.L
                nivel >= 9  -> Grado.M
                else        -> Grado.E
            }
            return Triple(fort, ref, vol)
        }

        // ✅ ALQUIMISTA (realista PF2e)
        if (esAlquimista(clase)) {
            val fort = if (nivel >= 11) Grado.E else Grado.T
            val ref  = if (nivel >= 7) Grado.E else Grado.T
            val vol  = if (nivel >= 9) Grado.E else Grado.T
            return Triple(fort, ref, vol)
        }

        // Guerrero (placeholder)
        if (esGuerrero(clase)) {
            val fort = if (nivel >= 9) Grado.E else Grado.T
            val ref  = if (nivel >= 9) Grado.E else Grado.T
            val vol  = Grado.T
            return Triple(fort, ref, vol)
        }

        return Triple(Grado.T, Grado.T, Grado.T)
    }
}



