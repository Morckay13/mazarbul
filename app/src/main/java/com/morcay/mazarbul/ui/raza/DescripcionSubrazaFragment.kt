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

            // 1) Leer la subraza que viene del paso anterior (SubrazaFragment)
            val subraza = findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("subrazaSeleccionada")

            // 2) Guardar esa subraza como dato "para atributos"
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("subrazaParaAtributos", subraza)

            // 3) Navegar a la pantalla de atributos
            findNavController().navigate(R.id.atributosFragment)
        }

    }

}