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

        // 1) RecyclerView
        recyclerView = view.findViewById(R.id.recyclerPersonajes)
        adapter = PersonajesAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 2) Botón + Añadir (navega a Golarion intro)
        val btnAdd = view.findViewById<Button>(R.id.btnAddCharacter)
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.golarionIntroFragment)
        }

        // 3) Cargar personajes al entrar
        cargarPersonajes()
    }

    override fun onResume() {
        super.onResume()
        // ✅ Para que al volver desde "Registrar" se recargue siempre
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
