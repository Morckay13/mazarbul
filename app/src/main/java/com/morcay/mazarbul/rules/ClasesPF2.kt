package com.morcay.mazarbul.rules

/**
 * Tabla de competencias por clase (PF2e).
 * Versión “nivel 1” (base) y preparada para escalar.
 *
 * NOTA: en PF2e la progresión real cambia con el nivel
 * (p.ej. algunas TS suben a Experto más tarde).
 * Ahora solo fijamos el “punto de partida” correcto por clase.
 */
object ClasesPF2 {

    fun competencias(clase: String?): CompetenciasClase {
        val c = clase?.trim()?.lowercase() ?: ""

        return when {

            // ✅ GUERRERO: Fortaleza y Percepción suelen ser fuertes
            c.contains("guerrer") -> CompetenciasClase(
                percepcion = GradoCompetencia.EXPERTO,
                fortaleza = GradoCompetencia.EXPERTO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.ENTRENADO
            )

            // ✅ MONJE: Reflejos fuerte y buena percepción
            c.contains("monje") -> CompetenciasClase(
                percepcion = GradoCompetencia.EXPERTO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.EXPERTO,
                voluntad = GradoCompetencia.ENTRENADO
            )

            // ✅ PÍCARO: Reflejos fuerte y percepción alta
            c.contains("pícar") || c.contains("picaro") -> CompetenciasClase(
                percepcion = GradoCompetencia.EXPERTO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.EXPERTO,
                voluntad = GradoCompetencia.ENTRENADO
            )

            // ✅ EXPLORADOR: Reflejos fuerte + buena percepción
            c.contains("explor") -> CompetenciasClase(
                percepcion = GradoCompetencia.EXPERTO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.EXPERTO,
                voluntad = GradoCompetencia.ENTRENADO
            )

            // ✅ CAMPEÓN: Fortaleza y Voluntad suelen ser fuertes
            c.contains("campe") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.EXPERTO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.EXPERTO
            )

            // ✅ CLÉRIGO: Voluntad fuerte
            c.contains("clér") || c.contains("clerig") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.NO_ENTRENADO,
                voluntad = GradoCompetencia.EXPERTO
            )

            // ✅ DRUIDA: Voluntad fuerte
            c.contains("druid") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.NO_ENTRENADO,
                voluntad = GradoCompetencia.EXPERTO
            )

            // ✅ BARBARO: Fortaleza fuerte
            c.contains("bárbar") || c.contains("barbar") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.EXPERTO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.NO_ENTRENADO
            )

            // ✅ ALQUIMISTA: Reflejos suele ser bueno
            c.contains("alquim") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.EXPERTO,
                voluntad = GradoCompetencia.NO_ENTRENADO
            )

            // ✅ BARDO: Voluntad fuerte
            c.contains("bardo") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.NO_ENTRENADO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.EXPERTO
            )

            // ✅ HECHICERO: Voluntad fuerte
            c.contains("hechicer") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.NO_ENTRENADO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.EXPERTO
            )

            // ✅ MAGO: Voluntad fuerte
            c.contains("mago") -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.NO_ENTRENADO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.EXPERTO
            )

            else -> CompetenciasClase(
                percepcion = GradoCompetencia.ENTRENADO,
                fortaleza = GradoCompetencia.ENTRENADO,
                reflejos = GradoCompetencia.ENTRENADO,
                voluntad = GradoCompetencia.ENTRENADO
            )
        }
    }
}
