package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.rules.SubopcionesPF2

class SubclaseFragment : Fragment(R.layout.fragment_subclase) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * ✅ 1) Recuperamos la clase elegida desde el fragment anterior
         */
        val claseSeleccionada = findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("claseSeleccionada")

        if (claseSeleccionada.isNullOrBlank()) {
            Toast.makeText(requireContext(), "No hay clase seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        /**
         * ✅ 2) Sacamos las subclases válidas para esa clase
         */
        val listaSubclases = SubopcionesPF2.subclasesDe(claseSeleccionada)

        if (listaSubclases.isEmpty()) {
            Toast.makeText(requireContext(), "No hay subclases para $claseSeleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        /**
         * ✅ 3) Botones del XML
         */
        val btn1 = view.findViewById<Button>(R.id.btnSub1)
        val btn2 = view.findViewById<Button>(R.id.btnSub2)
        val btn3 = view.findViewById<Button>(R.id.btnSub3)

        val botones = listOf(btn1, btn2, btn3)

        /**
         * ✅ 4) Asignamos texto y click a cada botón según la lista real
         * Si sobran botones, los ocultamos.
         */
        botones.forEachIndexed { index, boton ->
            if (index < listaSubclases.size) {
                val nombreSubclase = listaSubclases[index]
                boton.visibility = View.VISIBLE
                boton.text = nombreSubclase

                boton.setOnClickListener {
                    seleccionarSubclase(nombreSubclase, claseSeleccionada)
                }
            } else {
                boton.visibility = View.GONE
            }
        }
    }

    /**
     * Guarda subclase elegida y navega a pantalla de descripción
     */
    private fun seleccionarSubclase(nombre: String, claseSeleccionada: String) {

        // Guardamos subclase en el fragment actual (para usarla al volver)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.set("subclaseSeleccionada", nombre)

        // (Opcional) también guardamos clase, por si la descripción la usa
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.set("claseSeleccionada", claseSeleccionada)

        // Navegamos a descripción
        findNavController().navigate(R.id.descripcionSubclaseFragment)
    }
}


