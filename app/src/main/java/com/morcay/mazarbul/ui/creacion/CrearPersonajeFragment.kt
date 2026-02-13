package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.morcay.mazarbul.data.AppDatabase
import com.morcay.mazarbul.data.PersonajeEntity
import kotlinx.coroutines.launch




class CrearPersonajeFragment : Fragment() {

    private val personajeVM: PersonajeViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear_personaje, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRaza = view.findViewById<Button>(R.id.btnRaza)
        btnRaza.setOnClickListener {
            findNavController().navigate(R.id.razaFragment)
        }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<HashMap<String, Int>>("atributosFinales")
            ?.observe(viewLifecycleOwner) { atributos ->

                // Ejemplo: mostrar que ya están listos
                // Puedes cambiar esto por un TextView real
                Log.d("Atributos recibidos:" ,"$atributos")

                // Aquí podrías actualizar un texto tipo:
                // tvEstadoAtributos.text = "Atributos completados ✔"
            }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<HashMap<String, Int>>("atributosFinales")
            ?.observe(viewLifecycleOwner) { atributos ->

                // Aquí ya tienes el mapa
                // Ejemplo: mostrar estado, guardar en variables, etc.
                // tvEstadoAtributos.text = "Atributos completados ✔"
                println("ATRIBUTOS: $atributos")
            }
        val btnClase = view.findViewById<Button>(R.id.btnClase)

        btnClase.setOnClickListener {
            findNavController().navigate(R.id.claseFragment)
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("claseSeleccionada")
            ?.observe(viewLifecycleOwner) { clase ->
                // Ejemplo: actualizar UI
                // tvClase.text = clase
            }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("subclaseSeleccionada")
            ?.observe(viewLifecycleOwner) { subclase ->
                // actualizar UI
            }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<ArrayList<String>>("habilidadesSeleccionadas")
            ?.observe(viewLifecycleOwner) { habilidades ->
                // actualizar UI
            }

        val tvResumen = view.findViewById<TextView>(R.id.tvResumenRazaAtributos)

        fun renderResumen(
            raza: String?,
            subraza: String?,
            atributos: Map<String, Int>
        ) {
            val razaTxt = raza ?: "(no seleccionada)"
            val subrazaTxt = subraza ?: "-"

            val atributosTxt =
                if (atributos.isEmpty()) {
                    "-"
                } else {
                    // Orden fijo bonito
                    val orden = listOf("Fuerza", "Destreza", "Constitución", "Inteligencia", "Sabiduría", "Carisma")
                    orden.joinToString("\n") { key ->
                        val v = atributos[key] ?: 0
                        "$key: $v"
                    }
                }

            tvResumen.text = "Raza: $razaTxt\nSubraza: $subrazaTxt\n\nAtributos:\n$atributosTxt"
        }

// Observamos los 3 valores y repintamos cuando cambie cualquiera
        var cacheRaza: String? = null
        var cacheSubraza: String? = null
        var cacheAtributos: Map<String, Int> = emptyMap()

        personajeVM.raza.observe(viewLifecycleOwner) { r ->
            cacheRaza = r
            renderResumen(cacheRaza, cacheSubraza, cacheAtributos)
        }
        personajeVM.subraza.observe(viewLifecycleOwner) { s ->
            cacheSubraza = s
            renderResumen(cacheRaza, cacheSubraza, cacheAtributos)
        }
        personajeVM.atributosFinales.observe(viewLifecycleOwner) { a ->
            cacheAtributos = a
            renderResumen(cacheRaza, cacheSubraza, cacheAtributos)
        }

        val tvResumenClase = view.findViewById<TextView>(R.id.tvResumenClase)

        fun renderResumenClase(clase: String?, subclase: String?, habilidades: List<String>) {
            val claseTxt = clase ?: "(no seleccionada)"
            val subclaseTxt = subclase ?: "-"
            val habTxt = if (habilidades.isEmpty()) "-" else habilidades.joinToString(", ")

            tvResumenClase.text = "Clase: $claseTxt\nSubclase: $subclaseTxt\nHabilidades: $habTxt"
        }

// Cache local para repintar bien cuando cambie cualquiera
        var cacheClase: String? = null
        var cacheSubclase: String? = null
        var cacheHabilidades: List<String> = emptyList()

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

// Pintar una vez al entrar
        renderResumenClase(cacheClase, cacheSubclase, cacheHabilidades)

        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrarPersonaje)
        val etNombre = view.findViewById<EditText>(R.id.etNombrePersonaje)
        val etTrasfondo = view.findViewById<EditText>(R.id.etTrasfondoPersonaje)

        btnRegistrar.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val trasfondo = etTrasfondo.text.toString().trim()

            // Validaciones mínimas (para UX)
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

            // Construimos el “borrador” del personaje (todavía no Room)
            val personaje = PersonajeDraft(
                nombre = nombre,
                trasfondo = trasfondo,
                raza = raza,
                subraza = subraza,
                atributos = atributos,
                clase = clase,
                subclase = subclase,
                habilidades = habilidades
            )

            val atributosString = atributos.entries.joinToString(";") {
                "${it.key}:${it.value}"
            }

            val habilidadesString = habilidades.joinToString(",")

            val personajeEntity = PersonajeEntity(
                nombre = nombre,
                trasfondo = trasfondo,
                raza = raza,
                subraza = subraza,
                atributos = atributosString,
                clase = clase,
                subclase = subclase,
                habilidades = habilidadesString
            )

            val db = AppDatabase.getDatabase(requireContext())

            lifecycleScope.launch {
                db.personajeDao().insert(personajeEntity)

                Toast.makeText(
                    requireContext(),
                    "Personaje guardado correctamente",
                    Toast.LENGTH_LONG
                ).show()

                // ✅ Volver a la lista de personajes y limpiar el flujo de creación
                findNavController().popBackStack(R.id.personajesFragment, false)
            }
        }




    }
}