package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.rules.Ascendencia

/**
 * Selector de ascendencia/raza.
 * - Muestra la lista oficial desde Ascendencia (ReglasPf2.kt)
 * - Al pulsar, guarda "razaSeleccionada" en savedStateHandle
 * - Vuelve atrás al fragment anterior
 */
class SelectorRazaFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selector_raza, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listRazas)

        // ✅ Lista oficial (evita errores y duplicación)
        val listaRazas = Ascendencia.entries.map { it.nombre }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listaRazas
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val razaSeleccionada = listaRazas[position]

            // Guardamos el resultado para el fragment anterior (RazaFragment)
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("razaSeleccionada", razaSeleccionada)

            // Volvemos atrás
            findNavController().popBackStack()
        }
    }
}