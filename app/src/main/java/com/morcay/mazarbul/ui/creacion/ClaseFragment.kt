package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class ClaseFragment : Fragment(R.layout.fragment_clase) {

    // ViewModel compartido con CrearPersonajeFragment
    private val personajeVM: PersonajeViewModel by activityViewModels()

    // Cache local (lo que el usuario va eligiendo en el wizard)
    private var claseElegida: String? = null
    private var subclaseElegida: String? = null
    private var habilidadesElegidas: List<String> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDesc = view.findViewById<TextView>(R.id.tvDescripcionClase)
        val btnSeleccionarClase = view.findViewById<Button>(R.id.btnSeleccionarClase)
        val btnContinuarSubclase = view.findViewById<Button>(R.id.btnContinuarSubclase)
        val btnConfirmarFinal = view.findViewById<Button>(R.id.btnConfirmarClase)

        // 1) Ir a seleccionar clase
        btnSeleccionarClase.setOnClickListener {
            findNavController().navigate(R.id.selectorClaseFragment)
        }

        // 2) Ir a elegir subclase (tu flujo actual)
        btnContinuarSubclase.setOnClickListener {
            findNavController().navigate(R.id.subclaseFragment)
        }

        // 3) Escuchar CLASE (la setea SelectorClaseFragment en previousBackStackEntry)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("claseSeleccionada")
            ?.observe(viewLifecycleOwner) { clase ->
                claseElegida = clase
                refrescarResumen(tvDesc)
            }

        // 4) Escuchar SUBCLASE (la setea DescripcionSubclaseFragment en el savedStateHandle de claseFragment)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("subclaseSeleccionada")
            ?.observe(viewLifecycleOwner) { subclase ->
                subclaseElegida = subclase
                refrescarResumen(tvDesc)
            }

        // 5) Escuchar HABILIDADES (la setea DescripcionSubclaseFragment)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<ArrayList<String>>("habilidadesSeleccionadas")
            ?.observe(viewLifecycleOwner) { habilidades ->
                habilidadesElegidas = habilidades.toList()
                refrescarResumen(tvDesc)
            }

        // 6) Confirmar final: guardar TODO en ViewModel y volver a creación
        btnConfirmarFinal.setOnClickListener {
            personajeVM.setClaseCompleta(
                claseElegida,
                subclaseElegida,
                habilidadesElegidas
            )

            // Volver directo a CrearPersonajeFragment
            findNavController().popBackStack(R.id.crearPersonajeFragment, false)
        }

        // Pintado inicial
        refrescarResumen(tvDesc)
    }

    private fun refrescarResumen(tvDesc: TextView) {
        val c = claseElegida ?: "(sin clase)"
        val sc = subclaseElegida ?: "(sin subclase)"
        val hab = if (habilidadesElegidas.isEmpty()) "(sin habilidades)"
        else habilidadesElegidas.joinToString(", ")

        tvDesc.text = "Clase: $c\nSubclase: $sc\nHabilidades: $hab"
    }
}
