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
 *
 * Implementadas:
 * - Campeón (real)
 * - Guerrero (placeholder sólido)
 * - Monje (real)
 * - Clérigo (real + doctrina)
 * - Mago (real)
 * - Hechicero (real)
 * - Druida (real base)
 * - Bardo (real base)
 * - Alquimista (real base)
 * - Bárbaro (real)
 * - Explorador (real)
 * - Pícaro (real)
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

    // Clases
    fun esCampeon(clase: String?) = es(clase, "campeon")
    fun esGuerrero(clase: String?) = es(clase, "guerrero")
    fun esMonje(clase: String?) = es(clase, "monje")
    fun esClerigo(clase: String?) = es(clase, "clerigo")
    fun esMago(clase: String?) = es(clase, "mago")
    fun esHechicero(clase: String?) = es(clase, "hechicero")
    fun esDruida(clase: String?) = es(clase, "druida")
    fun esBardo(clase: String?) = es(clase, "bardo")
    fun esAlquimista(clase: String?) = es(clase, "alquimista")
    fun esBarbaro(clase: String?) = es(clase, "barbaro")
    fun esExplorador(clase: String?) = es(clase, "explorador")
    fun esPicaro(clase: String?) = es(clase, "picaro")

    // ------------------------------------------------------------
    // DEFENSA (CA)
    // ------------------------------------------------------------
    fun gradoDefensa(
        clase: String?,
        nivel: Int,
        categoria: ArmorCategory,
        subclase: String? = null
    ): Grado {

        // CAMPEÓN
        if (esCampeon(clase)) {
            return when {
                nivel >= 17 -> Grado.L
                nivel >= 13 -> Grado.M
                nivel >= 7  -> Grado.E
                else        -> Grado.T
            }
        }

        // GUERRERO
        if (esGuerrero(clase)) {
            return when {
                nivel >= 13 -> Grado.M
                nivel >= 11 -> Grado.E
                else -> Grado.T
            }
        }

        // MONJE
        if (esMonje(clase)) {
            return when (categoria) {
                ArmorCategory.UNARMORED -> when {
                    nivel >= 17 -> Grado.L
                    nivel >= 13 -> Grado.M
                    else -> Grado.E
                }
                else -> Grado.U
            }
        }

        // CLÉRIGO
        if (esClerigo(clase)) {
            if (categoria == ArmorCategory.UNARMORED) {
                return if (nivel >= 13) Grado.E else Grado.T
            }
            val guerra = esSub(subclase, "guerra")
            return if (guerra && categoria != ArmorCategory.HEAVY) Grado.T else Grado.U
        }

        // MAGO / HECHICERO
        if (esMago(clase) || esHechicero(clase)) {
            return if (categoria == ArmorCategory.UNARMORED) Grado.T else Grado.U
        }

        // DRUIDA
        if (esDruida(clase)) {
            val base = if (nivel >= 13) Grado.E else Grado.T
            return if (categoria == ArmorCategory.HEAVY) Grado.U else base
        }

        // BARDO
        if (esBardo(clase)) {
            val base = if (nivel >= 13) Grado.E else Grado.T
            return if (categoria == ArmorCategory.UNARMORED || categoria == ArmorCategory.LIGHT) base else Grado.U
        }

        // ALQUIMISTA
        if (esAlquimista(clase)) {
            val base = when {
                nivel >= 19 -> Grado.M
                nivel >= 13 -> Grado.E
                else -> Grado.T
            }
            return if (categoria == ArmorCategory.HEAVY) Grado.U else base
        }

        // BÁRBARO
        if (esBarbaro(clase)) {
            val base = if (nivel >= 13) Grado.E else Grado.T
            return if (categoria == ArmorCategory.HEAVY) Grado.U else base
        }

        // EXPLORADOR
        if (esExplorador(clase)) {
            val base = if (nivel >= 11) Grado.E else Grado.T
            return if (categoria == ArmorCategory.HEAVY) Grado.U else base
        }

        // PÍCARO
        if (esPicaro(clase)) {
            val base = if (nivel >= 13) Grado.E else Grado.T
            return if (categoria == ArmorCategory.UNARMORED || categoria == ArmorCategory.LIGHT) base else Grado.U
        }

        return if (categoria == ArmorCategory.UNARMORED) Grado.T else Grado.U
    }

    // ------------------------------------------------------------
    // PERCEPCIÓN
    // ------------------------------------------------------------
    fun gradoPercepcion(clase: String?, nivel: Int): Grado {

        if (esCampeon(clase)) return if (nivel >= 11) Grado.E else Grado.T
        if (esMonje(clase)) return if (nivel >= 5) Grado.E else Grado.T
        if (esClerigo(clase)) return if (nivel >= 5) Grado.E else Grado.T
        if (esMago(clase) || esHechicero(clase)) return if (nivel >= 11) Grado.E else Grado.T
        if (esDruida(clase)) return if (nivel >= 3) Grado.E else Grado.T
        if (esBardo(clase)) return if (nivel >= 11) Grado.M else Grado.E
        if (esAlquimista(clase)) return if (nivel >= 9) Grado.E else Grado.T
        if (esBarbaro(clase)) return if (nivel >= 7) Grado.E else Grado.T
        if (esExplorador(clase)) return if (nivel >= 1) Grado.E else Grado.T
        if (esPicaro(clase)) return if (nivel >= 7) Grado.E else Grado.T
        if (esGuerrero(clase)) return if (nivel >= 7) Grado.E else Grado.T

        return Grado.T
    }

    // ------------------------------------------------------------
    // TS
    // ------------------------------------------------------------
    fun gradosTS(clase: String?, nivel: Int): Triple<Grado, Grado, Grado> {

        if (esCampeon(clase)) {
            val fort = if (nivel >= 9) Grado.M else Grado.E
            val ref  = if (nivel >= 9) Grado.E else Grado.T
            val vol  = if (nivel >= 11) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        if (esMonje(clase)) {
            var fort = Grado.E
            var ref = Grado.E
            var vol = Grado.E
            if (nivel >= 7) ref = Grado.M
            if (nivel >= 11) vol = Grado.M
            if (nivel >= 15) ref = Grado.L
            return Triple(fort, ref, vol)
        }

        if (esClerigo(clase)) {
            return Triple(
                Grado.T,
                if (nivel >= 11) Grado.E else Grado.T,
                if (nivel >= 9) Grado.M else Grado.E
            )
        }

        if (esMago(clase) || esHechicero(clase)) {
            return Triple(
                Grado.T,
                if (nivel >= 15) Grado.M else Grado.E,
                if (nivel >= 9) Grado.M else Grado.E
            )
        }

        if (esDruida(clase)) {
            return Triple(
                Grado.E,
                if (nivel >= 9) Grado.E else Grado.T,
                if (nivel >= 11) Grado.M else Grado.E
            )
        }

        if (esBardo(clase)) {
            return Triple(
                if (nivel >= 9) Grado.E else Grado.T,
                if (nivel >= 3) Grado.E else Grado.T,
                Grado.E
            )
        }

        if (esAlquimista(clase)) {
            return Triple(
                Grado.E,
                Grado.E,
                if (nivel >= 7) Grado.E else Grado.T
            )
        }

        if (esBarbaro(clase)) {
            return Triple(
                Grado.E,
                if (nivel >= 7) Grado.E else Grado.T,
                Grado.T
            )
        }

        if (esExplorador(clase)) {
            return Triple(
                Grado.E,
                Grado.E,
                Grado.T
            )
        }

        if (esPicaro(clase)) {
            return Triple(
                Grado.T,
                Grado.E,
                Grado.E
            )
        }

        if (esGuerrero(clase)) {
            return Triple(
                if (nivel >= 9) Grado.E else Grado.T,
                if (nivel >= 9) Grado.E else Grado.T,
                Grado.T
            )
        }

        return Triple(Grado.T, Grado.T, Grado.T)
    }
}



