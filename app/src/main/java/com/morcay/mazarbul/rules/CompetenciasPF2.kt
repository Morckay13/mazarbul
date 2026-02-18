package com.morcay.mazarbul.rules

/**
 * Grados de competencia PF2e.
 * El bonus final se calcula como:
 * - No entrenado: 0
 * - Entrenado: nivel + 2
 * - Experto: nivel + 4
 * - Maestro: nivel + 6
 * - Legendario: nivel + 8
 */
enum class GradoCompetencia(val bonusBase: Int) {
    NO_ENTRENADO(0),
    ENTRENADO(2),
    EXPERTO(4),
    MAESTRO(6),
    LEGENDARIO(8)
}

/**
 * Competencias relevantes para cálculos “básicos” de ficha:
 * - Percepción
 * - Tiradas de salvación
 *
 * Más adelante añadiremos:
 * - Armaduras
 * - Armas
 * - CD de clase, etc.
 */
data class CompetenciasClase(
    val percepcion: GradoCompetencia,
    val fortaleza: GradoCompetencia,
    val reflejos: GradoCompetencia,
    val voluntad: GradoCompetencia
)

/**
 * Función helper: convierte grado + nivel -> bonus real.
 */
fun bonusCompetencia(grado: GradoCompetencia, nivel: Int): Int {
    return if (grado == GradoCompetencia.NO_ENTRENADO) 0 else (nivel + grado.bonusBase)
}
