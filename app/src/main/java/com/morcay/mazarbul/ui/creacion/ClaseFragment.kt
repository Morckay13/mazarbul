package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class ClaseFragment : Fragment(R.layout.fragment_clase) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSeleccionar = view.findViewById<Button>(R.id.btnSeleccionarClase)
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuarSubclase)

        btnSeleccionar.setOnClickListener {
            findNavController().navigate(R.id.selectorClaseFragment)
        }

        btnContinuar.setOnClickListener {
            findNavController().navigate(R.id.subclaseFragment)
        }

        // Escuchar clase elegida
        val tvDesc = view.findViewById<TextView>(R.id.tvDescripcionClase)

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("claseSeleccionada")
            ?.observe(viewLifecycleOwner) { clase ->
                tvDesc.text = "Clase elegida: $clase"
            }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("subclaseSeleccionada")
            ?.observe(viewLifecycleOwner) { subclase ->

                // Mostrar subclase elegida
                val tvDesc = view.findViewById<TextView>(R.id.tvDescripcionClase)
                tvDesc.text = "Subclase elegida: $subclase"
            }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<ArrayList<String>>("habilidadesSeleccionadas")
            ?.observe(viewLifecycleOwner) { habilidades ->
                // Ejemplo: mostrar en descripción
                val tvDesc = view.findViewById<TextView>(R.id.tvDescripcionClase)
                tvDesc.text = tvDesc.text.toString() + "\nHabilidades: ${habilidades.joinToString(", ")}"
            }

        val btnConfirmarClase = view.findViewById<Button>(R.id.btnConfirmarClase)

        btnConfirmarClase.setOnClickListener {

            // Recuperar datos guardados en este fragment
            val clase = findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("claseSeleccionada")

            val subclase = findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("subclaseSeleccionada")

            val habilidades = findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.get<ArrayList<String>>("habilidadesSeleccionadas")

            // Guardarlos en CrearPersonajeFragment
            val crearEntry = findNavController().getBackStackEntry(R.id.crearPersonajeFragment)

            crearEntry.savedStateHandle.set("claseSeleccionada", clase)
            crearEntry.savedStateHandle.set("subclaseSeleccionada", subclase)
            crearEntry.savedStateHandle.set("habilidadesSeleccionadas", habilidades)

            // Volver directamente a CrearPersonajeFragment
            findNavController().popBackStack(R.id.crearPersonajeFragment, false)
        }
    }
}