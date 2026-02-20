package com.morcay.mazarbul.ui.ficha

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.morcay.mazarbul.R

class FichaPersonajeFragment : Fragment(R.layout.fragment_ficha_personaje) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val personajeId = arguments?.getInt("personajeId", -1) ?: -1
        if (personajeId == -1) return

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        val adapter = FichaPagerAdapter(this, personajeId)
        viewPager.adapter = adapter

        val titulos = listOf("Ficha", "Equipo", "Conjuros", "Notas")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titulos[position]
        }.attach()
    }
}