package com.morcay.mazarbul.ui.ficha

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.morcay.mazarbul.ui.detalle.DetallePersonajeFragment

class FichaPagerAdapter(
    fragment: Fragment,
    private val personajeId: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val f: Fragment = when (position) {
            0 -> DetallePersonajeFragment()        // ✅ Reutilizamos lo que ya funciona
            1 -> PlaceholderFragment.newInstance("Equipo (pendiente)")
            2 -> PlaceholderFragment.newInstance("Conjuros (pendiente)")
            else -> PlaceholderFragment.newInstance("Notas (pendiente)")
        }

        // ✅ Pasamos personajeId a TODOS los fragments
        f.arguments = (f.arguments ?: Bundle()).apply {
            putInt("personajeId", personajeId)
        }

        return f
    }
}