package com.morcay.mazarbul.ui.detalle

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.AppDatabase
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle simple del personaje (versión 1).
 * Recibe "personajeId" por arguments y carga desde Room.
 */
class DetallePersonajeFragment : Fragment(R.layout.fragment_detalle_personaje) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Recoger ID enviado desde la lista
        val personajeId = arguments?.getInt("personajeId", -1) ?: -1
        if (personajeId == -1) return

        // 2) Referencias UI
        val img = view.findViewById<ImageView>(R.id.imgPersonaje)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvRaza = view.findViewById<TextView>(R.id.tvRaza)
        val tvClase = view.findViewById<TextView>(R.id.tvClase)
        val tvNivel = view.findViewById<TextView>(R.id.tvNivel)
        val tvTrasfondo = view.findViewById<TextView>(R.id.tvTrasfondo)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
        val btnEditar = view.findViewById<Button>(R.id.btnEditar)

        // 3) Cargar desde Room
        val db = AppDatabase.getDatabase(requireContext())

        // 4) Cargar personaje desde Room y pintar datos
        lifecycleScope.launch {
            val p = db.personajeDao().getById(personajeId) ?: return@launch

            tvNombre.text = p.nombre
            tvRaza.text = "Raza: ${p.raza} • ${p.subraza}"
            tvClase.text = "Clase: ${p.clase} • ${p.subclase}"
            //tvNivel.text = "Nivel: ${p.nivel}"
            tvTrasfondo.text = p.trasfondo.ifBlank { "Sin trasfondo" }

            img.setImageResource(R.mipmap.ic_launcher)
        }

        // ✅ 5) BOTÓN EDITAR
        // Navega a CrearPersonajeFragment pasando el id para entrar en "modo edición"
        btnEditar.setOnClickListener {
            val b = Bundle().apply { putInt("personajeId", personajeId) }
            findNavController().navigate(R.id.crearPersonajeFragment, b)
        }

        // ✅ 6) BOTÓN ELIMINAR
        // Mostramos un diálogo de confirmación antes de borrar.
        btnEliminar.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar personaje")
                .setMessage("¿Seguro que quieres eliminar este personaje? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->

                    // Borramos en segundo plano (corrutina)
                    lifecycleScope.launch {
                        db.personajeDao().deleteById(personajeId)

                        Toast.makeText(
                            requireContext(),
                            "Personaje eliminado",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Volvemos a la lista. Usamos popBackStack simple:
                        findNavController().popBackStack()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
