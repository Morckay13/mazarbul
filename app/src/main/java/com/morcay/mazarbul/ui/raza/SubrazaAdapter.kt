package com.morcay.mazarbul.ui.raza

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morcay.mazarbul.R

class SubrazaAdapter(
    private val items: List<SubrazaItem>,
    private val onClick: (SubrazaItem) -> Unit
) : RecyclerView.Adapter<SubrazaAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgSubraza)
        val nombre: TextView = view.findViewById(R.id.tvNombreSubraza)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subraza, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.nombre.text = item.nombre
        holder.img.setImageResource(item.imageRes)

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
