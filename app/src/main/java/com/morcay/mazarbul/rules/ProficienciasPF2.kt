package com.morcay.mazarbul.rules

/**
 * Grados de competencia PF2e.
 * U = Untrained, T = Trained, E = Expert, M = Master, L = Legendary
 */
enum class Grado { U, T, E, M, L }

/**
 * Bonus de competencia PF2e: grado + nivel (salvo Untrained).
 * - U: 0
 * - T: nivel + 2
 * - E: nivel + 4
 * - M: nivel + 6
 * - L: nivel + 8
 */
fun bonusCompetencia(grado: Grado, nivel: Int): Int = when (grado) {
    Grado.U -> 0
    Grado.T -> nivel + 2
    Grado.E -> nivel + 4
    Grado.M -> nivel + 6
    Grado.L -> nivel + 8
}

/**
 * Categoría de armadura a efectos de proficiencia.
 * (La usamos en CA para decidir qué proficiencia aplica)
 */
enum class ArmorCategory { UNARMORED, LIGHT, MEDIUM, HEAVY }

/**
 * Proficiencias por clase.
 * De momento implementamos SOLO Campeón (como me pediste).
 *
 * NOTA IMPORTANTE:
 * - Aquí definimos la progresión REAL por nivel.
 * - Para el resto de clases aún devolvemos valores "razonables" de fallback.
 */
object ProficienciasPF2 {

    /** Normaliza nombres: "Campeón" -> "campeon" */
    private fun norm(s: String?): String = s?.trim()?.lowercase()
        ?.replace("á", "a")?.replace("é", "e")?.replace("í", "i")
        ?.replace("ó", "o")?.replace("ú", "u")
        ?.replace("ñ", "n")
        ?: ""

    fun esCampeon(clase: String?): Boolean {
        val c = norm(clase)
        return c == "campeon" || c.contains("campeon")
    }

    /**
     * ✅ DEFENSA (CA): grado de proficiencia en defensa según CLASE + NIVEL + CATEGORÍA.
     *
     * CAMPEÓN (real PF2e):
     * - nivel 1: Trained en unarmored, light, medium, heavy
     * - nivel 7: Expert en todas
     * - nivel 13: Master en todas
     * - nivel 17: Legendary en todas
     */
    fun gradoDefensa(clase: String?, nivel: Int, categoria: ArmorCategory): Grado {
        if (esCampeon(clase)) {
            return when {
                nivel >= 17 -> Grado.L
                nivel >= 13 -> Grado.M
                nivel >= 7 -> Grado.E
                else -> Grado.T
            }
        }

        // ✅ Fallback temporal para otras clases (hasta implementarlas bien)
        // - Sin armadura: entrenado
        // - Armaduras: no entrenado (salvo que más adelante lo afinemos)
        return when (categoria) {
            ArmorCategory.UNARMORED -> Grado.T
            else -> Grado.U
        }
    }

    /**
     * ✅ PERCEPCIÓN: grado por clase + nivel
     *
     * CAMPEÓN:
     * - nivel 1: Trained
     * - nivel 11: Expert
     */
    fun gradoPercepcion(clase: String?, nivel: Int): Grado {
        if (esCampeon(clase)) {
            return if (nivel >= 11) Grado.E else Grado.T
        }
        return Grado.T // fallback
    }

    /**
     * ✅ TS: Fort / Ref / Will por grado según clase + nivel
     *
     * CAMPEÓN:
     * - nivel 1: Fort E, Ref T, Will E
     * - nivel 9: Fort M, Ref E
     * - nivel 11: Will M
     */
    fun gradosTS(clase: String?, nivel: Int): Triple<Grado, Grado, Grado> {
        if (esCampeon(clase)) {
            val fort = if (nivel >= 9) Grado.M else Grado.E
            val ref = if (nivel >= 9) Grado.E else Grado.T
            val vol = if (nivel >= 11) Grado.M else Grado.E
            return Triple(fort, ref, vol)
        }

        // fallback: todo entrenado
        return Triple(Grado.T, Grado.T, Grado.T)
    }
}