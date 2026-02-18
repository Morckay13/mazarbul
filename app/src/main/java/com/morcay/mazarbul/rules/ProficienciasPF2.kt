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

/** Paquete de TS (Fort, Ref, Will). */
data class TS(val fort: Int, val ref: Int, val vol: Int)

/**
 * Proficiencias por clase (PF2e).
 *
 * ✅ Implementado "real" con progresión por nivel para:
 * - Campeón
 * - Guerrero (Fighter)
 * - Monje
 *
 * Para el resto de clases devolvemos un fallback razonable (T en unarmored y TS/Percepción T).
 * (Cuando quieras, vamos clase por clase y lo dejamos igual de “real”).
 *
 * NOTA (Monje):
 * Path to Perfection (niveles 7/11/15) depende de elección del jugador.
 * Como aún NO guardamos esa elección, usamos un DEFAULT:
 * - Nivel 7: Ref -> Master
 * - Nivel 11: Will -> Master
 * - Nivel 15: Ref -> Legendary
 */
object ProficienciasPF2 {

    // ------------------------------------------------------------
    // Normalización
    // ------------------------------------------------------------

    /** Normaliza: "Campeón" -> "campeon", quita acentos y ñ */
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

    private fun esCampeon(clase: String?) = es(clase, "campeon")
    private fun esGuerrero(clase: String?) = es(clase, "guerrero")
    private fun esMonje(clase: String?) = es(clase, "monje")

    // ------------------------------------------------------------
    // DEFENSA (CA): grado por clase + nivel + categoría de armadura
    // ------------------------------------------------------------

    /**
     * Grado de defensa (CA) según clase + nivel + categoría.
     *
     * - CAMPEÓN:
     *   * Inicial: T en todas las armaduras y unarmored
     *   * 7:  Armor Expertise -> E
     *   * 13: Armor Mastery   -> M
     *   * 17: Legendary Armor -> L
     *
     * - GUERRERO (Fighter):
     *   * Inicial: T en todas las armaduras y unarmored
     *   * 11: Armor Expertise -> E
     *   * 17: Armor Mastery   -> M
     *
     * - MONJE:
     *   * Unarmored: E inicial, 13 -> M, 17 -> L
     *   * Armaduras: U (todas)
     */
    fun gradoDefensa(clase: String?, nivel: Int, categoria: ArmorCategory): Grado {

        // ✅ CAMPEÓN
        if (esCampeon(clase)) {
            return when {
                nivel >= 17 -> Grado.L
                nivel >= 13 -> Grado.M
                nivel >= 7 -> Grado.E
                else -> Grado.T
            }
        }

        // ✅ GUERRERO (Fighter)
        if (esGuerrero(clase)) {
            // Fighter: trained al inicio; 11 -> expert; 17 -> master
            return when {
                nivel >= 17 -> Grado.M
                nivel >= 11 -> Grado.E
                else -> Grado.T
            }
        }

        // ✅ MONJE
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

        // Fallback temporal (resto clases)
        return when (categoria) {
            ArmorCategory.UNARMORED -> Grado.T
            else -> Grado.U
        }
    }

    // ------------------------------------------------------------
    // PERCEPCIÓN: grado por clase + nivel
    // ------------------------------------------------------------

    /**
     * - CAMPEÓN: T inicial, 11 -> E
     * - GUERRERO: E inicial, 7 -> M
     * - MONJE:    T inicial, 5 -> E
     */
    fun gradoPercepcion(clase: String?, nivel: Int): Grado {

        if (esCampeon(clase)) {
            return if (nivel >= 11) Grado.E else Grado.T
        }

        if (esGuerrero(clase)) {
            return if (nivel >= 7) Grado.M else Grado.E
        }

        if (esMonje(clase)) {
            return if (nivel >= 5) Grado.E else Grado.T
        }

        return Grado.T
    }

    // ------------------------------------------------------------
    // TS: grados por clase + nivel
    // ------------------------------------------------------------

    /**
     * Devuelve (Fort, Ref, Will).
     *
     * CAMPEÓN:
     * - Nivel 1: Fort E, Ref T, Will E
     * - Nivel 9: Ref E, Fort M
     * - Nivel 11: Will M
     *
     * GUERRERO (Fighter):
     * - Nivel 1: Fort E, Ref E, Will T
     * - Nivel 3: Will E
     * - Nivel 9: Fort M
     * - Nivel 15: Ref M
     *
     * MONJE:
     * - Nivel 1: Fort E, Ref E, Will E
     * - Nivel 7/11/15: Path to Perfection (depende de elección; usamos DEFAULT)
     */
    fun gradosTS(clase: String?, nivel: Int): Triple<Grado, Grado, Grado> {

        // ✅ CAMPEÓN
        if (esCampeon(clase)) {
            val fort = if (nivel >= 9) Grado.M else Grado.E
            val ref = if (nivel >= 9) Grado.E else Grado.T
            val vol = if (nivel >= 11) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // ✅ GUERRERO (Fighter)
        if (esGuerrero(clase)) {
            val fort = when {
                nivel >= 9 -> Grado.M
                else -> Grado.E
            }
            val ref = when {
                nivel >= 15 -> Grado.M
                else -> Grado.E
            }
            val vol = when {
                nivel >= 3 -> Grado.E
                else -> Grado.T
            }
            return Triple(fort, ref, vol)
        }

        // ✅ MONJE (Path to Perfection con default)
        if (esMonje(clase)) {
            var fort = Grado.E
            var ref = Grado.E
            var vol = Grado.E

            // DEFAULT sin guardar elección
            if (nivel >= 7) ref = Grado.M
            if (nivel >= 11) vol = Grado.M
            if (nivel >= 15) ref = Grado.L

            return Triple(fort, ref, vol)
        }

        // Fallback
        return Triple(Grado.T, Grado.T, Grado.T)
    }
}