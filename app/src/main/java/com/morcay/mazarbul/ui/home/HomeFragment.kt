package com.morcay.mazarbul.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.morcay.mazarbul.R
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_home -> {
                    Toast.makeText(requireContext(), "Inicio", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_personajes -> {
                    findNavController().navigate(R.id.personajesFragment)
                }

                R.id.nav_social -> {
                    Toast.makeText(requireContext(), "Social", Toast.LENGTH_SHORT).show()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

}
