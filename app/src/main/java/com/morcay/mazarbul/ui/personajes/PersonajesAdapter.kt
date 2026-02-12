package com.morcay.mazarbul.ui.personajes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morcay.mazarbul.R

class PersonajesAdapter(
    private val lista: List<String>
) : RecyclerView.Adapter<PersonajesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val detalles: TextView = view.findViewById(R.id.tvDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_personaje, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personaje = lista[position]
        holder.nombre.text = personaje
        holder.detalles.text = "Humano • Guerrero • Nivel 1"
    }

    override fun getItemCount() = lista.size
}

