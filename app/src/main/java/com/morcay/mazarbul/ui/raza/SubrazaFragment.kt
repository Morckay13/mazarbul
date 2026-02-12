package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.morcay.mazarbul.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubrazaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubrazaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subraza, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSubrazas)

        // 2 columnas en el grid
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        // Lista de prueba
        val subrazas = listOf(
            SubrazaItem("Subraza A", R.drawable.ic_launcher_background),
            SubrazaItem("Subraza B", R.drawable.ic_launcher_background),
            SubrazaItem("Subraza C", R.drawable.ic_launcher_background),
            SubrazaItem("Subraza D", R.drawable.ic_launcher_background),
        )

        recycler.adapter = SubrazaAdapter(subrazas) { seleccion ->
            // Por ahora solo mostramos un Toast o volvemos
            Toast.makeText(requireContext(), "Elegido: ${seleccion.nombre}", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubrazaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubrazaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}