package com.morcay.mazarbul.ui.detalle

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.AppDatabase
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle simple del personaje (versión 1).
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
        val tvAtributos = view.findViewById<TextView>(R.id.tvAtributos)
        val tvIniciativa = view.findViewById<TextView>(R.id.tvIniciativa)
        val tvCA = view.findViewById<TextView>(R.id.tvCA)
        val tvPG = view.findViewById<TextView>(R.id.tvPG)
        val tvPercepcion = view.findViewById<TextView>(R.id.tvPercepcion)
        val tvVelocidad = view.findViewById<TextView>(R.id.tvVelocidad)



        // 3) Cargar desde Room
        val db = AppDatabase.getDatabase(requireContext())

        // 4) Cargar personaje desde Room y pintar datos
        lifecycleScope.launch {
            val p = db.personajeDao().getById(personajeId) ?: return@launch

            // Datos básicos
            tvNombre.text = p.nombre
            tvRaza.text = "Raza: ${p.raza} • ${p.subraza}"
            tvClase.text = "Clase: ${p.clase} • ${p.subclase}"
            // tvNivel.text = "Nivel: ${p.nivel}"  // si lo vuelves a activar
            tvTrasfondo.text = p.trasfondo.ifBlank { "Sin trasfondo" }

            // ✅ Atributos
            val atributosMap = parseAtributos(p.atributos)
            val orden =
                listOf("Fuerza", "Destreza", "Constitución", "Inteligencia", "Sabiduría", "Carisma")

            val textoAtributos = orden.joinToString("\n") { key ->
                val v = atributosMap[key] ?: 0
                "$key: $v"
            }

            tvAtributos.text = textoAtributos

            //Habilidades

            val tvHabilidades = view.findViewById<TextView>(R.id.tvHabilidades)

            val habilidades = parseHabilidades(p.habilidades)

            tvHabilidades.text =
                if (habilidades.isEmpty()) {
                    "Sin habilidades seleccionadas"
                } else {
                    habilidades.joinToString("\n")
                }

            // ✅ Modificadores (PF2 = (atributo - 10) / 2 redondeando hacia abajo)
            val modDes = calcularModificador(atributosMap["Destreza"] ?: 10)
            val modCon = calcularModificador(atributosMap["Constitución"] ?: 10)

            // ✅ Iniciativa (de momento: solo Destreza)
            tvIniciativa.text = "Iniciativa: ${formatearBonus(modDes)}"

            // ✅ CA (placeholder simple: 10 + DEX)
            // Más adelante: armadura + competencia + etc.
            val ca = 10 + modDes
            tvCA.text = "Clase de Armadura: $ca"

            // ✅ PV/PG (placeholder por ahora)
            // En PF2 depende de clase + CON + ascendencia + nivel, etc.
            tvPG.text = "Puntos de golpe: (pendiente de cálculo)"

            // ✅ Percepción (placeholder simple):
            // En PF2 normalmente depende de competencia + SAB (Wisdom).
            // Ahora: solo usamos SAB para que ya se vea algo coherente.
            val modSab = calcularModificador(atributosMap["Sabiduría"] ?: 10)
            tvPercepcion.text = "Percepción: ${formatearBonus(modSab)} (base)"

            // ✅ Velocidad (placeholder simple):
            // En PF2 depende de ascendencia/raza (y a veces armadura, dotes...).
            // Ahora: una tabla mínima por raza común, si no, 25 ft.
            val velocidad = calcularVelocidadBase(p.raza)
            tvVelocidad.text = "Velocidad: $velocidad ft (base)"



            // Imagen fija por ahora
            img.setImageResource(R.mipmap.ic_launcher)

            // ✅ 5) BOTÓN EDITAR
            // Navega a CrearPersonajeFragment pasando el id para entrar en "modo edición"
            btnEditar.setOnClickListener {
                val b = Bundle().apply { putInt("personajeId", personajeId) }
                findNavController().navigate(R.id.crearPersonajeFragment, b)
            }

            // ✅ 6) BOTÓN ELIMINAR
            // Mostramos un diálogo de confirmación antes de borrar.
            btnEliminar.setOnClickListener {

                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar personaje")
                    .setMessage("¿Seguro que quieres eliminar este personaje? Esta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar") { _, _ ->

                        // Borramos en segundo plano (corrutina)
                        lifecycleScope.launch {
                            db.personajeDao().deleteById(personajeId)

                            Toast.makeText(
                                requireContext(),
                                "Personaje eliminado",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Volvemos a la lista. Usamos popBackStack simple:
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

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
        return raw.split(",").map { it.trim() }
    }

    private fun calcularModificador(score: Int): Int {
        // Ej: 18 -> +4, 12 -> +1, 9 -> -1
        return Math.floor((score - 10) / 2.0).toInt()
    }

    private fun formatearBonus(valor: Int): String {
        return if (valor >= 0) "+$valor" else valor.toString()
    }

    private fun calcularVelocidadBase(raza: String?): Int {
        val r = raza?.trim()?.lowercase() ?: return 25

        // Tabla mínima (placeholder). Ajustaremos con tu lista real más adelante.
        return when {
            r.contains("enano") -> 20
            r.contains("dwarf") -> 20
            r.contains("elfo") -> 30
            r.contains("elf") -> 30
            r.contains("humano") -> 25
            r.contains("human") -> 25
            r.contains("gnomo") -> 25
            r.contains("gnome") -> 25
            r.contains("mediano") -> 25
            r.contains("halfling") -> 25
            else -> 25
        }
    }



}
