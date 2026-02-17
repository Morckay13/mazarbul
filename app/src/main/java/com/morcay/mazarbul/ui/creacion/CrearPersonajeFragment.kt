package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.AppDatabase
import com.morcay.mazarbul.data.PersonajeEntity
import kotlinx.coroutines.launch

/**
 * CrearPersonajeFragment reutilizado para:
 * - CREAR personaje (insert)
 * - EDITAR personaje (update)
 *
 * Si llega arguments["personajeId"] => EDICIÓN
 * Si no llega => CREACIÓN
 */
class CrearPersonajeFragment : Fragment(R.layout.fragment_crear_personaje) {

    private val personajeVM: PersonajeViewModel by activityViewModels()

    // Se calculan en onViewCreated (arguments aquí ya es seguro)
    private var personajeId: Int = -1
    private var esEdicion: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 0) Detectar modo edición/creación
        personajeId = arguments?.getInt("personajeId", -1) ?: -1
        esEdicion = personajeId != -1

        // DB
        val db = AppDatabase.getDatabase(requireContext())

        // Referencias UI
        val btnRaza = view.findViewById<Button>(R.id.btnRaza)
        val btnClase = view.findViewById<Button>(R.id.btnClase)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrarPersonaje)

        val etNombre = view.findViewById<EditText>(R.id.etNombrePersonaje)
        val etTrasfondo = view.findViewById<EditText>(R.id.etTrasfondoPersonaje)

        val tvResumenRazaAtributos = view.findViewById<TextView>(R.id.tvResumenRazaAtributos)
        val tvResumenClase = view.findViewById<TextView>(R.id.tvResumenClase)

        // ✅ 1) Si es edición, cambia el texto del botón (opcional pero recomendable)
        if (esEdicion) {
            btnRegistrar.text = "Guardar cambios"
        } else {
            btnRegistrar.text = "Registrar personaje"
        }

        // ✅ 2) Navegación a pantallas de selección
        btnRaza.setOnClickListener { findNavController().navigate(R.id.razaFragment) }
        btnClase.setOnClickListener { findNavController().navigate(R.id.claseFragment) }

        /**
         * ✅ 3) (Opcional) Log de atributos al volver desde atributosFragment
         * IMPORTANTE: tenías este observer duplicado. Lo dejamos una sola vez.
         */
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<HashMap<String, Int>>("atributosFinales")
            ?.observe(viewLifecycleOwner) { atributos ->
                Log.d("CrearPersonaje", "Atributos recibidos: $atributos")
            }

        /**
         * ✅ 4) Render de resumen RAZA/SUBRAZA/ATRIBUTOS (se repinta cuando cambie algo)
         */
        fun renderResumenRaza(raza: String?, subraza: String?, atributos: Map<String, Int>) {
            val razaTxt = raza ?: "(no seleccionada)"
            val subrazaTxt = subraza ?: "-"

            val atributosTxt =
                if (atributos.isEmpty()) {
                    "-"
                } else {
                    val orden = listOf("Fuerza", "Destreza", "Constitución", "Inteligencia", "Sabiduría", "Carisma")
                    orden.joinToString("\n") { key ->
                        val v = atributos[key] ?: 0
                        "$key: $v"
                    }
                }

            tvResumenRazaAtributos.text =
                "Raza: $razaTxt\nSubraza: $subrazaTxt\n\nAtributos:\n$atributosTxt"
        }

        var cacheRaza: String? = personajeVM.raza.value
        var cacheSubraza: String? = personajeVM.subraza.value
        var cacheAtributos: Map<String, Int> = personajeVM.atributosFinales.value ?: emptyMap()

        personajeVM.raza.observe(viewLifecycleOwner) { r ->
            cacheRaza = r
            renderResumenRaza(cacheRaza, cacheSubraza, cacheAtributos)
        }
        personajeVM.subraza.observe(viewLifecycleOwner) { s ->
            cacheSubraza = s
            renderResumenRaza(cacheRaza, cacheSubraza, cacheAtributos)
        }
        personajeVM.atributosFinales.observe(viewLifecycleOwner) { a ->
            cacheAtributos = a
            renderResumenRaza(cacheRaza, cacheSubraza, cacheAtributos)
        }

        // Pintado inicial
        renderResumenRaza(cacheRaza, cacheSubraza, cacheAtributos)

        /**
         * ✅ 5) Render de resumen CLASE/SUBCLASE/HABILIDADES
         */
        fun renderResumenClase(clase: String?, subclase: String?, habilidades: List<String>) {
            val claseTxt = clase ?: "(no seleccionada)"
            val subclaseTxt = subclase ?: "-"
            val habTxt = if (habilidades.isEmpty()) "-" else habilidades.joinToString(", ")

            tvResumenClase.text = "Clase: $claseTxt\nSubclase: $subclaseTxt\nHabilidades: $habTxt"
        }

        var cacheClase: String? = personajeVM.clase.value
        var cacheSubclase: String? = personajeVM.subclase.value
        var cacheHabilidades: List<String> = personajeVM.habilidades.value ?: emptyList()

        personajeVM.clase.observe(viewLifecycleOwner) {
            cacheClase = it
            renderResumenClase(cacheClase, cacheSubclase, cacheHabilidades)
        }
        personajeVM.subclase.observe(viewLifecycleOwner) {
            cacheSubclase = it
            renderResumenClase(cacheClase, cacheSubclase, cacheHabilidades)
        }
        personajeVM.habilidades.observe(viewLifecycleOwner) {
            cacheHabilidades = it
            renderResumenClase(cacheClase, cacheSubclase, cacheHabilidades)
        }

        // Pintado inicial
        renderResumenClase(cacheClase, cacheSubclase, cacheHabilidades)

        /**
         * ✅ 6) Si es EDICIÓN, cargamos el personaje desde Room y rellenamos:
         * - EditTexts
         * - ViewModel (para que el resumen muestre lo guardado)
         */
        if (esEdicion) {
            lifecycleScope.launch {
                val p = db.personajeDao().getById(personajeId) ?: return@launch

                // EditTexts
                etNombre.setText(p.nombre)
                etTrasfondo.setText(p.trasfondo)

                // ✅ ViewModel (con tus setters actuales)
                personajeVM.setRazaSubraza(p.raza, p.subraza)
                personajeVM.setAtributosFinales(parseAtributos(p.atributos))
                personajeVM.setClaseCompleta(
                    p.clase,
                    p.subclase,
                    parseHabilidades(p.habilidades)
                )
            }
        }


        /**
         * ✅ 7) Guardar (INSERT si crear, UPDATE si editar)
         */
        btnRegistrar.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val trasfondo = etTrasfondo.text.toString().trim()

            if (nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce un nombre de personaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val raza = personajeVM.raza.value
            val subraza = personajeVM.subraza.value
            val atributos = personajeVM.atributosFinales.value ?: emptyMap()

            val clase = personajeVM.clase.value
            val subclase = personajeVM.subclase.value
            val habilidades = personajeVM.habilidades.value ?: emptyList()

            // Validaciones mínimas
            if (raza.isNullOrBlank() || subraza.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Completa la Raza y Subraza", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (atributos.isEmpty()) {
                Toast.makeText(requireContext(), "Completa los Atributos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (clase.isNullOrBlank() || subclase.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Completa la Clase y Subclase", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (habilidades.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona las habilidades", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Serializamos atributos/habilidades a String para Room
            val atributosString = atributos.entries.joinToString(";") { "${it.key}:${it.value}" }
            val habilidadesString = habilidades.joinToString(",")

            // ✅ Nivel: si ya lo tienes en tu entidad, mantenlo
            // Si aún no calculas nivel, por defecto 1
            val nivel = 1

            // ✅ CLAVE: si es edición mantenemos el ID para que Room actualice, no cree otro
            val personajeEntity = PersonajeEntity(
                id = if (esEdicion) personajeId else 0,
                nombre = nombre,
                trasfondo = trasfondo,
                raza = raza,
                subraza = subraza,
                atributos = atributosString,
                clase = clase,
                subclase = subclase,
                habilidades = habilidadesString,
                nivel = nivel
            )

            lifecycleScope.launch {
                if (esEdicion) {
                    db.personajeDao().update(personajeEntity)
                    Toast.makeText(requireContext(), "Cambios guardados", Toast.LENGTH_SHORT).show()
                } else {
                    db.personajeDao().insert(personajeEntity)
                    Toast.makeText(requireContext(), "Personaje guardado correctamente", Toast.LENGTH_SHORT).show()
                }

                // Volver a la lista de personajes
                findNavController().popBackStack(R.id.personajesFragment, false)
            }
        }
    }

    /**
     * Convierte "Fuerza:15;Destreza:14;..." en Map<String, Int>
     */
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

    /**
     * Convierte "Acrobacias,Atletismo,..." en List<String>
     */
    private fun parseHabilidades(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        return raw.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }
}
