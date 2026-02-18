package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.rules.ClasePF2

/**
 * Selector de clase.
 * - Muestra la lista oficial desde ClasePF2 (ReglasPf2.kt)
 * - Al pulsar, guarda "claseSeleccionada" en savedStateHandle
 * - Vuelve atrás al fragment anterior (ClaseFragment)
 */
class SelectorClaseFragment : Fragment(R.layout.fragment_selector_clase) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listClases)

        // ✅ Lista oficial de clases PF2e
        val listaClases = ClasePF2.entries.map { it.nombre }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listaClases
        )

        listView.adapter = adapter

        // Click -> guardar y volver
        listView.setOnItemClickListener { _, _, position, _ ->
            val claseSeleccionada = listaClases[position]

            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("claseSeleccionada", claseSeleccionada)

            findNavController().popBackStack()
        }
    }
}

