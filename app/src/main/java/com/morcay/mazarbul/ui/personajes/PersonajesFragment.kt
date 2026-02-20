package com.morcay.mazarbul.ui.personajes

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.AppDatabase
import kotlinx.coroutines.launch

class PersonajesFragment : Fragment(R.layout.fragment_personajes) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PersonajesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerPersonajes)
        adapter = PersonajesAdapter(emptyList()) { personaje ->

            // Bundle con el id del personaje
            val b = Bundle().apply {
                putInt("personajeId", personaje.id)
            }

            // Navegar a la ficha/detalle
            findNavController().navigate(R.id.fichaPersonajeFragment, b)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val btnAdd = view.findViewById<Button>(R.id.btnAddCharacter)
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.golarionIntroFragment)
        }

        cargarPersonajes()
    }

    override fun onResume() {
        super.onResume()
        cargarPersonajes()
    }

    private fun cargarPersonajes() {
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val lista = db.personajeDao().getAll()
            adapter.actualizar(lista)
        }
    }
}

