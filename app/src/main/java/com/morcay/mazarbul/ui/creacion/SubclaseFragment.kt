package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class SubclaseFragment : Fragment(R.layout.fragment_subclase) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Esta función guarda la subclase elegida
         * y vuelve a la pantalla anterior (ClaseFragment)
         */
        fun seleccionarSubclase(nombre: String) {
            // Guardamos la subclase elegida en el estado del fragment actual
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("subclaseSeleccionada", nombre)

            // Navegamos a la pantalla de descripción
            findNavController().navigate(R.id.descripcionSubclaseFragment)
        }

        // Conectamos los botones
        view.findViewById<Button>(R.id.btnSub1).setOnClickListener {
            seleccionarSubclase("Campeón")
        }

        view.findViewById<Button>(R.id.btnSub2).setOnClickListener {
            seleccionarSubclase("Hechicero")
        }

        view.findViewById<Button>(R.id.btnSub3).setOnClickListener {
            seleccionarSubclase("Sacerdote")
        }
    }
}
