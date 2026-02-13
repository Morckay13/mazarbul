package com.morcay.mazarbul.ui.personajes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morcay.mazarbul.R
import com.morcay.mazarbul.data.PersonajeEntity

class PersonajesAdapter(
    private var lista: List<PersonajeEntity>
) : RecyclerView.Adapter<PersonajesAdapter.PersonajeVH>() {

    class PersonajeVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgPersonaje)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvInfo: TextView = itemView.findViewById(R.id.tvInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonajeVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_personaje, parent, false)
        return PersonajeVH(v)
    }

    override fun onBindViewHolder(holder: PersonajeVH, position: Int) {
        val p = lista[position]

        holder.tvNombre.text = p.nombre
        holder.tvInfo.text = "${p.raza} • ${p.clase}"

        // Por ahora imagen fija
        holder.img.setImageResource(R.mipmap.ic_launcher)
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<PersonajeEntity>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}

