package com.morcay.mazarbul.ui.detalle

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.AppDatabase
import com.morcay.mazarbul.rules.Ascendencia
import com.morcay.mazarbul.rules.ClasePF2
import kotlinx.coroutines.launch
import kotlin.math.floor

/**
 * Pantalla de detalle del personaje.
 * Recibe "personajeId" por arguments y carga desde Room.
 */
class DetallePersonajeFragment : Fragment(R.layout.fragment_detalle_personaje) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Recoger ID enviado desde la lista
        val personajeId = arguments?.getInt("personajeId", -1) ?: -1
        if (personajeId == -1) return

        // 2) Referencias UI
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

        // Tiradas de salvación (TS)
        val tvFortaleza = view.findViewById<TextView>(R.id.tvFortaleza)
        val tvReflejos = view.findViewById<TextView>(R.id.tvReflejos)
        val tvVoluntad = view.findViewById<TextView>(R.id.tvVoluntad)

        // 3) DB
        val db = AppDatabase.getDatabase(requireContext())

        // ✅ BOTÓN EDITAR
        btnEditar.setOnClickListener {
            val b = Bundle().apply { putInt("personajeId", personajeId) }
            findNavController().navigate(R.id.crearPersonajeFragment, b)
        }

        // ✅ BOTÓN ELIMINAR
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

        // 4) Cargar personaje desde Room y pintar datos
        lifecycleScope.launch {
            val p = db.personajeDao().getById(personajeId) ?: return@launch

            // ===== Datos básicos =====
            tvNombre.text = p.nombre
            tvRaza.text = "Raza: ${p.raza} • ${p.subraza}"
            tvClase.text = "Clase: ${p.clase} • ${p.subclase}"

            val nivel = p.nivel
            tvNivel.text = "Nivel: $nivel"

            tvTrasfondo.text = p.trasfondo.ifBlank { "Sin trasfondo" }

            // ===== Atributos =====
            val atributosMap = parseAtributos(p.atributos)

            pintarAtributosTabla(
                inflater = layoutInflater,
                container = containerAtributos,
                atributos = atributosMap
            )

            // ===== Habilidades (bonus real PF2e básico) =====
            val habilidades = parseHabilidades(p.habilidades)

            pintarHabilidadesTabla(
                inflater = layoutInflater,
                container = containerHabilidades,
                habilidades = habilidades,
                atributos = atributosMap,
                nivel = nivel
            )

            // ===== Mods base =====
            val modDes = calcularModificador(atributosMap["Destreza"] ?: 10)
            val modCon = calcularModificador(atributosMap["Constitución"] ?: 10)
            val modSab = calcularModificador(atributosMap["Sabiduría"] ?: 10)

            // ===== Iniciativa (por ahora solo DEX) =====
            tvIniciativa.text = "Iniciativa: ${formatearBonus(modDes)}"

            // ===== CA (placeholder simple: 10 + DEX) =====
            val ca = 10 + modDes
            tvCA.text = "Clase de Armadura: $ca"

            // ===== Percepción (PF2e básico asumiendo entrenada) =====
            // Percepción = SAB + (nivel + 2)
            val percepcionTotal = modSab + (nivel + 2)
            tvPercepcion.text = "Percepción: ${formatearBonus(percepcionTotal)}"

            // ===== TS (PF2e básico asumiendo entrenadas) =====
            val compEntrenado = nivel + 2
            tvFortaleza.text = "Fortaleza: ${formatearBonus(modCon + compEntrenado)}"
            tvReflejos.text = "Reflejos: ${formatearBonus(modDes + compEntrenado)}"
            tvVoluntad.text = "Voluntad: ${formatearBonus(modSab + compEntrenado)}"

            // ===== Velocidad (desde catálogo Ascendencia) =====
            val asc = Ascendencia.fromNombre(p.raza)
            val velocidad = asc?.velocidad ?: 25
            tvVelocidad.text = "Velocidad: $velocidad ft (base)"

            // ===== PG reales PF2e básico =====
            // PG = PG_Ascendencia + (PG_Clase + Mod_CON) * Nivel
            val clase = ClasePF2.fromNombre(p.clase)

            val pgAsc = asc?.pgBase ?: 8
            val pgClase = clase?.pgPorNivel ?: 8

            val pgTotal = pgAsc + ((pgClase + modCon) * nivel)

            // Mostramos desglose (queda genial y ayuda a depurar)
            tvPG.text = "PG: $pgTotal  (Asc: $pgAsc, Clase: $pgClase/nivel, CON: ${formatearBonus(modCon)})"

            // Imagen fija por ahora
            img.setImageResource(R.mipmap.ic_launcher)
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
    // CÁLCULOS BÁSICOS PF2
    // ----------------------------

    private fun calcularModificador(score: Int): Int {
        // PF2: (score - 10) / 2 redondeando hacia abajo
        return floor((score - 10) / 2.0).toInt()
    }

    private fun formatearBonus(valor: Int): String {
        return if (valor >= 0) "+$valor" else valor.toString()
    }

    private fun abreviarAtributo(atributo: String): String {
        return when (atributo) {
            "Fuerza" -> "Fue"
            "Destreza" -> "Des"
            "Constitución" -> "Con"
            "Inteligencia" -> "Int"
            "Sabiduría" -> "Sab"
            "Carisma" -> "Car"
            else -> atributo.take(3)
        }
    }

    // ----------------------------
    // UI HELPERS (PINTAR TABLAS)
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

            val tvNombre = fila.findViewById<TextView>(R.id.tvNombreAtrib)
            val tvValor = fila.findViewById<TextView>(R.id.tvValorAtrib)
            val tvMod = fila.findViewById<TextView>(R.id.tvModAtrib)

            tvNombre.text = nombre
            tvValor.text = valor.toString()
            tvMod.text = formatearBonus(mod)

            container.addView(fila)
        }
    }

    /**
     * BONUS REAL (versión básica PF2e):
     * - Habilidades guardadas -> asumimos "Entrenada"
     * - Entrenada: nivel + 2
     * - Total = mod atributo + competencia
     */
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

            // Entrenadas porque fueron seleccionadas en la creación
            tvEntrenada.text = "EN"

            val atributo = atributoDeHabilidad(hab)
            val mod = calcularModificador(atributos[atributo] ?: 10)

            // Competencia entrenada PF2e
            val competencia = nivel + 2

            val total = mod + competencia

            tvNombre.text = "$hab (${abreviarAtributo(atributo)})"
            tvBonus.text = formatearBonus(total)

            container.addView(fila)
        }
    }

    private fun atributoDeHabilidad(habilidad: String): String {
        return when (habilidad.lowercase()) {
            "atletismo" -> "Fuerza"
            "acrobacias", "sigilo", "latrocinio" -> "Destreza"
            "arcano", "ocultismo", "religión", "sociedad" -> "Inteligencia"
            "naturaleza", "supervivencia", "medicina" -> "Sabiduría"
            "interpretación", "engaño", "intimidación", "diplomacia" -> "Carisma"
            else -> "Destreza"
        }
    }
}

