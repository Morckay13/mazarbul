package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GolarionIntroFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_golarion_intro, container, false)
    }

    // ✅ AQUÍ AÑADIMOS LA CONEXIÓN DEL BOTÓN
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCrear = view.findViewById<Button>(R.id.btnCrearPersonaje)
        btnCrear.setOnClickListener {
            findNavController().navigate(R.id.crearPersonajeFragment)
        }
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: String) =
            GolarionIntroFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
