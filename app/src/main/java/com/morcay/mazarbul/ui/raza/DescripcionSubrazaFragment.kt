package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
class DescripcionSubrazaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_descripcion_subraza, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombreSubrazaDetalle)

        val subraza = findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("subrazaSeleccionada")

        tvNombre.text = subraza ?: "Subraza"

        val btnContinuar = view.findViewById<Button>(R.id.btnContinuarAtributos)

        btnContinuar.setOnClickListener {

            // Subraza (ya lo tenías)
            val subraza = findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("subrazaSeleccionada")

            // Raza: viene de pasos anteriores. Normalmente está guardada en el backstack
            val raza = findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("razaSeleccionada")

            // Guardamos ambos para Atributos
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("subrazaParaAtributos", subraza)

            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("razaParaAtributos", raza)

            findNavController().navigate(R.id.atributosFragment)
        }


    }
    
}