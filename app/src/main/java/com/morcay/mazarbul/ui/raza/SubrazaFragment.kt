package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.morcay.mazarbul.R
import com.morcay.mazarbul.rules.SubopcionesPF2

class SubrazaFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subraza, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSubrazas)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        /**
         * ✅ 1) Recuperamos la raza elegida (guardada anteriormente)
         * Normalmente la guardaste desde SelectorRazaFragment y la recogió RazaFragment.
         *
         * Aquí la leemos desde el fragment anterior (previousBackStackEntry),
         * porque esta pantalla depende de ese dato.
         */
        val razaSeleccionada = findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("razaSeleccionada")

        if (razaSeleccionada.isNullOrBlank()) {
            Toast.makeText(requireContext(), "No hay raza seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        /**
         * ✅ 2) Obtenemos SOLO las subrazas válidas para esa raza
         */
        val nombresSubrazas = SubopcionesPF2.subrazasDe(razaSeleccionada)

        if (nombresSubrazas.isEmpty()) {
            Toast.makeText(requireContext(), "No hay subrazas para $razaSeleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        /**
         * ✅ 3) Convertimos a SubrazaItem (tu modelo visual)
         * Por ahora usamos un drawable genérico.
         * Luego podrás cambiarlo por imágenes reales por subraza.
         */
        val subrazas = nombresSubrazas.map { nombre ->
            SubrazaItem(nombre, R.drawable.ic_launcher_background)
        }

        /**
         * ✅ 4) Adapter: al seleccionar una subraza:
         * - La guardamos en savedStateHandle (currentBackStackEntry)
         * - Navegamos a la descripción
         */
        recycler.adapter = SubrazaAdapter(subrazas) { seleccion ->

            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("subrazaSeleccionada", seleccion.nombre)

            // (Opcional) también guardamos la raza, por si la descripción la necesita
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("razaSeleccionada", razaSeleccionada)

            findNavController().navigate(R.id.descripcionSubrazaFragment)
        }
    }
}

