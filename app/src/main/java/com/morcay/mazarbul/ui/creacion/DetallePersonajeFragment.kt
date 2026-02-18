package com.morcay.mazarbul.ui.detalle

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.AppDatabase
import com.morcay.mazarbul.rules.Ascendencia
import com.morcay.mazarbul.rules.ClasePF2
import com.morcay.mazarbul.rules.ArmorCategory
import com.morcay.mazarbul.rules.ProficienciasPF2
import com.morcay.mazarbul.rules.bonusCompetencia
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.min

/**
 * Pantalla de detalle del personaje.
 *
 * ✅ CA PF2e base (sin inventario real todavía):
 * CA = 10 + itemBonus(armadura) + DEX(capeada) + competencia + escudo(si levantado)
 *
 * - itemBonus + dexCap dependen de la armadura elegida en el Spinner (temporal)
 * - competencia depende de la CLASE y la CATEGORÍA de armadura (U/T/E/M/L) + NIVEL
 * - escudo: +2 si está levantado (temporal)
 *
 * ✅ PF2e REAL implementado por ahora para CAMPEÓN (progresión por nivel incluida).
 */
class DetallePersonajeFragment : Fragment(R.layout.fragment_detalle_personaje) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Recoger ID
        val personajeId = arguments?.getInt("personajeId", -1) ?: -1
        if (personajeId == -1) return

        // 2) UI
        val img = view.findViewById<ImageView>(R.id.imgPersonaje)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvRaza = view.findViewById<TextView>(R.id.tvRaza)
        val tvClase = view.findViewById<TextView>(R.id.tvClase)
        val tvNivel = view.findViewById<TextView>(R.id.tvNivel)
        val tvTrasfondo = view.findViewById<TextView>(R.id.tvTrasfondo)

        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
        val btnEditar = view.findViewById<Button>(R.id.btnEditar)

        val containerAtributos = view.findViewById<LinearLayout>(R.id.containerAtributos)
        val containerHabilidades = view.findViewById<LinearLayout>(R.id.containerHabilidades)

        val tvIniciativa = view.findViewById<TextView>(R.id.tvIniciativa)
        val tvCA = view.findViewById<TextView>(R.id.tvCA)
        val tvPG = view.findViewById<TextView>(R.id.tvPG)
        val tvPercepcion = view.findViewById<TextView>(R.id.tvPercepcion)
        val tvVelocidad = view.findViewById<TextView>(R.id.tvVelocidad)

        val tvFortaleza = view.findViewById<TextView>(R.id.tvFortaleza)
        val tvReflejos = view.findViewById<TextView>(R.id.tvReflejos)
        val tvVoluntad = view.findViewById<TextView>(R.id.tvVoluntad)

        // ✅ Escudo
        val swEscudo = view.findViewById<SwitchCompat>(R.id.swEscudoLevantado)

        // ✅ Armadura (Spinner)
        val spArmadura = view.findViewById<Spinner>(R.id.spArmadura)

        // 3) DB
        val db = AppDatabase.getDatabase(requireContext())

        // Botón editar
        btnEditar.setOnClickListener {
            val b = Bundle().apply { putInt("personajeId", personajeId) }
            findNavController().navigate(R.id.crearPersonajeFragment, b)
        }

        // Botón eliminar
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar personaje")
                .setMessage("¿Seguro que quieres eliminar este personaje? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    lifecycleScope.launch {
                        db.personajeDao().deleteById(personajeId)
                        Toast.makeText(requireContext(), "Personaje eliminado", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // 4) Cargar personaje y pintar
        lifecycleScope.launch {
            val p = db.personajeDao().getById(personajeId) ?: return@launch

            tvNombre.text = p.nombre
            tvRaza.text = "Raza: ${p.raza} • ${p.subraza}"
            tvClase.text = "Clase: ${p.clase} • ${p.subclase}"

            val nivel = p.nivel
            tvNivel.text = "Nivel: $nivel"
            tvTrasfondo.text = p.trasfondo.ifBlank { "Sin trasfondo" }

            // Atributos
            val atributosMap = parseAtributos(p.atributos)
            pintarAtributosTabla(layoutInflater, containerAtributos, atributosMap)

            // Habilidades (por ahora: entrenadas si están guardadas)
            val habilidades = parseHabilidades(p.habilidades)
            pintarHabilidadesTabla(layoutInflater, containerHabilidades, habilidades, atributosMap, nivel)

            // Mods base
            val modDes = calcularModificador(atributosMap["Destreza"] ?: 10)
            val modCon = calcularModificador(atributosMap["Constitución"] ?: 10)
            val modSab = calcularModificador(atributosMap["Sabiduría"] ?: 10)

            // Iniciativa (todavía simple)
            tvIniciativa.text = "Iniciativa: ${formatearBonus(modDes)}"

            // Percepción PF2e REAL (por clase/nivel)
            val gradoPerc = ProficienciasPF2.gradoPercepcion(p.clase, nivel)
            val compPerc = bonusCompetencia(gradoPerc, nivel)
            tvPercepcion.text = "Percepción: ${formatearBonus(modSab + compPerc)} ($gradoPerc)"

            // TS PF2e REAL (por clase/nivel)
            val (gFort, gRef, gVol) = ProficienciasPF2.gradosTS(p.clase, nivel)

            val fortTotal = modCon + bonusCompetencia(gFort, nivel)
            val refTotal = modDes + bonusCompetencia(gRef, nivel)
            val volTotal = modSab + bonusCompetencia(gVol, nivel)

            tvFortaleza.text = "Fortaleza: ${formatearBonus(fortTotal)} ($gFort)"
            tvReflejos.text = "Reflejos: ${formatearBonus(refTotal)} ($gRef)"
            tvVoluntad.text = "Voluntad: ${formatearBonus(volTotal)} ($gVol)"

            // Velocidad
            val asc = Ascendencia.fromNombre(p.raza)
            val velocidad = asc?.velocidad ?: 25
            tvVelocidad.text = "Velocidad: $velocidad ft (base)"

            // PG básico
            val clase = ClasePF2.fromNombre(p.clase)
            val pgAsc = asc?.pgBase ?: 8
            val pgClase = clase?.pgPorNivel ?: 8
            val pgTotal = pgAsc + ((pgClase + modCon) * nivel)
            tvPG.text = "PG: $pgTotal  (Asc: $pgAsc, Clase: $pgClase/nivel, CON: ${formatearBonus(modCon)})"

            // Imagen fija
            img.setImageResource(R.mipmap.ic_launcher)

            // =========================================================
            // ✅ CA PF2e REAL (por clase/nivel) + ARMADURA + ESCUDO
            // =========================================================

            // 1) Armaduras temporales
            val armaduras = listOf(
                ArmorBase.noArmor(),
                ArmorBase.leatherArmor(),
                ArmorBase.chainMail(),
                ArmorBase.fullPlate()
            )

            // 2) Spinner
            val nombres = armaduras.map { it.nombre }
            spArmadura.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                nombres
            )

            // 3) Recalcular CA
            fun recalcularYMostrarCA(armadura: ArmorBase, escudoLevantado: Boolean) {

                // 3.1 DEX cap
                val dexAplicada = if (armadura.dexCap == null) modDes else min(modDes, armadura.dexCap)

                // 3.2 Grado defensa REAL (Campeón real + fallback resto)
                val gradoDefensa = ProficienciasPF2.gradoDefensa(
                    clase = p.clase,
                    nivel = nivel,
                    categoria = armadura.categoria
                )

                // 3.3 Bonus competencia
                val compDefensa = bonusCompetencia(gradoDefensa, nivel)

                // 3.4 Escudo (+2)
                val bonusEscudo = if (escudoLevantado) 2 else 0

                // 3.5 CA final
                val ca = 10 + armadura.itemBonus + dexAplicada + compDefensa + bonusEscudo

                // Mostrar desglose
                tvCA.text =
                    "Clase de Armadura: $ca (10 + item:${armadura.itemBonus} + DEX:${formatearBonus(dexAplicada)} + $gradoDefensa:$compDefensa + escudo:$bonusEscudo)"
            }

            // Inicial
            var armaduraSeleccionada = armaduras[0]
            recalcularYMostrarCA(armaduraSeleccionada, swEscudo.isChecked)

            // Cambios armadura
            spArmadura.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    armaduraSeleccionada = armaduras[position]
                    recalcularYMostrarCA(armaduraSeleccionada, swEscudo.isChecked)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // Cambios escudo
            swEscudo.setOnCheckedChangeListener { _, isChecked ->
                recalcularYMostrarCA(armaduraSeleccionada, isChecked)
            }
        }
    }

    // ----------------------------
    // MODELO ARMADURA (temporal)
    // ----------------------------
    data class ArmorBase(
        val nombre: String,
        val categoria: ArmorCategory,
        val itemBonus: Int,
        val dexCap: Int?
    ) {
        companion object {
            fun noArmor() = ArmorBase("Sin armadura", ArmorCategory.UNARMORED, 0, null)

            // Valores típicos para probar el motor
            fun leatherArmor() = ArmorBase("Cuero (Ligera)", ArmorCategory.LIGHT, 1, 4)
            fun chainMail() = ArmorBase("Cota de mallas (Media)", ArmorCategory.MEDIUM, 4, 1)
            fun fullPlate() = ArmorBase("Placas (Pesada)", ArmorCategory.HEAVY, 6, 0)
        }
    }

    // ----------------------------
    // PARSERS
    // ----------------------------
    private fun parseAtributos(raw: String): Map<String, Int> {
        if (raw.isBlank()) return emptyMap()
        val mapa = mutableMapOf<String, Int>()
        raw.split(";").forEach { item ->
            val kv = item.split(":")
            if (kv.size == 2) {
                val key = kv[0].trim()
                val value = kv[1].trim().toIntOrNull() ?: 0
                mapa[key] = value
            }
        }
        return mapa
    }

    private fun parseHabilidades(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        return raw.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

    // ----------------------------
    // CÁLCULOS
    // ----------------------------
    private fun calcularModificador(score: Int): Int =
        floor((score - 10) / 2.0).toInt()

    private fun formatearBonus(valor: Int): String =
        if (valor >= 0) "+$valor" else valor.toString()

    // ----------------------------
    // UI HELPERS
    // ----------------------------
    private fun pintarAtributosTabla(
        inflater: LayoutInflater,
        container: LinearLayout,
        atributos: Map<String, Int>
    ) {
        container.removeAllViews()
        val orden = listOf("Fuerza", "Destreza", "Constitución", "Inteligencia", "Sabiduría", "Carisma")

        for (nombre in orden) {
            val valor = atributos[nombre] ?: 10
            val mod = calcularModificador(valor)

            val fila = inflater.inflate(R.layout.item_atributo_detalle, container, false)
            fila.findViewById<TextView>(R.id.tvNombreAtrib).text = nombre
            fila.findViewById<TextView>(R.id.tvValorAtrib).text = valor.toString()
            fila.findViewById<TextView>(R.id.tvModAtrib).text = formatearBonus(mod)
            container.addView(fila)
        }
    }

    private fun pintarHabilidadesTabla(
        inflater: LayoutInflater,
        container: LinearLayout,
        habilidades: List<String>,
        atributos: Map<String, Int>,
        nivel: Int
    ) {
        container.removeAllViews()

        for (hab in habilidades) {
            val fila = inflater.inflate(R.layout.item_habilidad_detalle, container, false)

            val tvEntrenada = fila.findViewById<TextView>(R.id.tvEntrenada)
            val tvNombre = fila.findViewById<TextView>(R.id.tvNombreHab)
            val tvBonus = fila.findViewById<TextView>(R.id.tvBonusHab)

            tvEntrenada.text = "EN"

            val atributo = atributoDeHabilidad(hab)
            val mod = calcularModificador(atributos[atributo] ?: 10)

            val competencia = nivel + 2
            tvNombre.text = "$hab (${atributo.take(3)})"
            tvBonus.text = formatearBonus(mod + competencia)

            container.addView(fila)
        }
    }

    private fun atributoDeHabilidad(habilidad: String): String {
        return when (normalizar(habilidad)) {
            "atletismo" -> "Fuerza"
            "acrobacias", "sigilo", "latrocinio" -> "Destreza"
            "arcano", "ocultismo", "religion", "sociedad" -> "Inteligencia"
            "naturaleza", "supervivencia", "medicina" -> "Sabiduría"
            "interpretacion", "engano", "intimidacion", "diplomacia" -> "Carisma"
            else -> "Destreza"
        }
    }

    private fun normalizar(texto: String?): String {
        val t = (texto ?: "").trim().lowercase()
        return t
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
    }
}
