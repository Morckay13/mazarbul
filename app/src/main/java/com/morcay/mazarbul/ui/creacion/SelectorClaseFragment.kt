package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class SelectorClaseFragment : Fragment(R.layout.fragment_selector_clase) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Función para seleccionar una clase y volver atrás
        fun seleccionarClase(clase: String) {

            // Guardamos el resultado para el fragment anterior (ClaseFragment)
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("claseSeleccionada", clase)

            // Volvemos a ClaseFragment
            findNavController().popBackStack()
        }

        // Botones de selección
        view.findViewById<Button>(R.id.btnGuerrero).setOnClickListener {
            seleccionarClase("Guerrero")
        }

        view.findViewById<Button>(R.id.btnMago).setOnClickListener {
            seleccionarClase("Mago")
        }

        view.findViewById<Button>(R.id.btnClerigo).setOnClickListener {
            seleccionarClase("Clérigo")
        }
    }
}

