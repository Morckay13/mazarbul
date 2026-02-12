package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RazaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RazaFragment : Fragment(R.layout.fragment_raza) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RazaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RazaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}