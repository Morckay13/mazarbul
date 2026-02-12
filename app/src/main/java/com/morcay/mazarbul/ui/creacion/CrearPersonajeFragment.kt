package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import androidx.fragment.app.activityViewModels


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
    }
}