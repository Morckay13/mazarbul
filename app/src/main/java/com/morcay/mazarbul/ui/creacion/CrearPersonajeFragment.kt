package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class CrearPersonajeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear_personaje, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRaza = view.findViewById<Button>(R.id.btnRaza)
        btnRaza.setOnClickListener {
            findNavController().navigate(R.id.razaFragment)
        }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<HashMap<String, Int>>("atributosFinales")
            ?.observe(viewLifecycleOwner) { atributos ->

                // Ejemplo: mostrar que ya están listos
                // Puedes cambiar esto por un TextView real
                Log.d("Atributos recibidos:" ,"$atributos")

                // Aquí podrías actualizar un texto tipo:
                // tvEstadoAtributos.text = "Atributos completados ✔"
            }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<HashMap<String, Int>>("atributosFinales")
            ?.observe(viewLifecycleOwner) { atributos ->

                // Aquí ya tienes el mapa
                // Ejemplo: mostrar estado, guardar en variables, etc.
                // tvEstadoAtributos.text = "Atributos completados ✔"
                println("ATRIBUTOS: $atributos")
            }
        val btnClase = view.findViewById<Button>(R.id.btnClase)

        btnClase.setOnClickListener {
            findNavController().navigate(R.id.claseFragment)
        }

    }

}