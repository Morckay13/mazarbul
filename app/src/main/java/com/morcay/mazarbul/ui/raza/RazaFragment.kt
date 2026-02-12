package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class RazaFragment : Fragment(R.layout.fragment_raza) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSeleccionar = view.findViewById<Button>(R.id.btnSeleccionarRaza)

        btnSeleccionar.setOnClickListener {
            findNavController().navigate(R.id.selectorRazaFragment)
        }
        val tvDesc = view.findViewById<TextView>(R.id.tvDescripcionRaza)

        // ✅ Recibe la raza elegida
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("razaSeleccionada")
            ?.observe(viewLifecycleOwner) { raza ->
                tvDesc.text = "Has seleccionado: $raza\n\n(Aquí irá la descripción más adelante)"
            }

        // ✅ Botón continuar -> SubrazaFragment
        val btnContinuar = view.findViewById<Button>(R.id.btnContinuarSubraza)

        btnContinuar.setOnClickListener {
            findNavController().navigate(R.id.subrazaFragment)
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("subrazaSeleccionada")
            ?.observe(viewLifecycleOwner) { subraza ->
                tvDesc.text = tvDesc.text.toString() + "\n\nSubraza: $subraza"
            }

    }
}