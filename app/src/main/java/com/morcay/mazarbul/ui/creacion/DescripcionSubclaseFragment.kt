package com.morcay.mazarbul.ui.creacion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R

class DescripcionSubclaseFragment : Fragment(R.layout.fragment_descripcion_subclase) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a UI
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreSubclaseDetalle)
        val tvHabilidades = view.findViewById<TextView>(R.id.tvHabilidadesElegidas)
        val btnHabilidades = view.findViewById<Button>(R.id.btnHabilidades)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarClase)

        // 1) Recuperar la subclase seleccionada desde SubclaseFragment
        val subclase = findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("subclaseSeleccionada")

        tvNombre.text = subclase ?: "Subclase"

        // 2) Lista de habilidades de prueba (luego la haremos dependiente de clase/subclase)
        val listaHabilidades = arrayOf(
            "Atletismo",
            "Acrobacias",
            "Sigilo",
            "Arcano",
            "Naturaleza",
            "Medicina"
        )

        // ✅ 3) ESTA VARIABLE TIENE QUE ESTAR AQUÍ (ANTES DEL LISTENER)
        // Guardará lo que el usuario seleccione en el diálogo
        var habilidadesSeleccionadas: List<String> = emptyList()

        // Mostrar estado inicial
        tvHabilidades.text = "Habilidades elegidas: (ninguna)"

        // 4) Botón que abre el diálogo de habilidades
        btnHabilidades.setOnClickListener {

            // Marcado inicial: si ya había habilidades marcadas, las mostramos como checkeadas
            val seleccionInicial = BooleanArray(listaHabilidades.size) { index ->
                listaHabilidades[index] in habilidadesSeleccionadas
            }

            val dialog = HabilidadesDialogFragment(
                habilidades = listaHabilidades,
                maxSeleccion = 2, // por ahora máximo 2; luego lo haremos variable
                seleccionInicial = seleccionInicial,
                onConfirmar = { seleccionadas ->

                    // Guardamos selección en memoria
                    habilidadesSeleccionadas = seleccionadas

                    // Mostramos selección
                    tvHabilidades.text = "Habilidades elegidas: " +
                            if (seleccionadas.isEmpty()) "(ninguna)"
                            else seleccionadas.joinToString(", ")
                }
            )

            dialog.show(parentFragmentManager, "HabilidadesDialog")
        }

        // 5) Confirmar clase: guardar subclase y habilidades en ClaseFragment y volver
        btnConfirmar.setOnClickListener {

            // Guardar subclase en ClaseFragment
            findNavController().getBackStackEntry(R.id.claseFragment)
                .savedStateHandle
                .set("subclaseSeleccionada", subclase)

            // Guardar habilidades en ClaseFragment
            findNavController().getBackStackEntry(R.id.claseFragment)
                .savedStateHandle
                .set("habilidadesSeleccionadas", ArrayList(habilidadesSeleccionadas))

            // Volver a ClaseFragment (NO a creación)
            findNavController().popBackStack(R.id.claseFragment, false)
        }
    }
}

