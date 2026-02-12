package com.morcay.mazarbul.ui.raza

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.morcay.mazarbul.R
import androidx.fragment.app.activityViewModels
import com.morcay.mazarbul.ui.creacion.PersonajeViewModel



class AtributosFragment : Fragment(R.layout.fragment_atributos) {


    private val personajeVM: PersonajeViewModel by activityViewModels()
    private val atributosFinales = mutableMapOf<String, Int>()
    private var puntosRestantes = 27
    private val minAtributo = 8
    private val maxAtributo = 15

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvResumen = view.findViewById<TextView>(R.id.tvResumen)
        val tvPuntos = view.findViewById<TextView>(R.id.tvPuntosTotales)

        val raza = findNavController()
            .getBackStackEntry(R.id.razaFragment)
            .savedStateHandle
            .get<String>("razaSeleccionada")
            ?: "-"

        // Recibir datos del flujo
        val subraza = findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("subrazaParaAtributos") ?: "-"

        tvResumen.text = "Raza: $raza | Subraza: $subraza"

        val bonus = calcularBonusRazaSubraza(raza, subraza)

        fun actualizarPuntosUI() {
            tvPuntos.text = "Puntos restantes: $puntosRestantes"
        }

       // val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarAtributos)

        //btnConfirmar.setOnClickListener {

            // 1) Guardar el resultado DIRECTAMENTE en CrearPersonajeFragment
            //val crearEntry = findNavController().getBackStackEntry(R.id.crearPersonajeFragment)

            //crearEntry.savedStateHandle.set(
               // "atributosFinales",
               // HashMap(atributosFinales)
          //  )

            // 2) Volver directamente a CrearPersonajeFragment (saltando los intermedios)
          //  findNavController().popBackStack(R.id.crearPersonajeFragment, false)
      //  }


        actualizarPuntosUI()

        // Configurar filas
        configurarFila(view.findViewById(R.id.rowFuerza), "Fuerza", bonus["Fuerza"] ?: 0) { actualizarPuntosUI() }
        configurarFila(view.findViewById(R.id.rowDestreza), "Destreza", bonus["Destreza"] ?: 0) { actualizarPuntosUI() }
        configurarFila(view.findViewById(R.id.rowConstitucion), "Constitución", bonus["Constitución"] ?: 0) { actualizarPuntosUI() }
        configurarFila(view.findViewById(R.id.rowInteligencia), "Inteligencia", bonus["Inteligencia"] ?: 0) { actualizarPuntosUI() }
        configurarFila(view.findViewById(R.id.rowSabiduria), "Sabiduría", bonus["Sabiduría"] ?: 0) { actualizarPuntosUI() }
        configurarFila(view.findViewById(R.id.rowCarisma), "Carisma", bonus["Carisma"] ?: 0) { actualizarPuntosUI() }

        // Recuperamos raza y subraza desde el backstack
        val razaSeleccionada = findNavController()
            .getBackStackEntry(R.id.razaFragment)
            .savedStateHandle
            .get<String>("razaSeleccionada")

        val subrazaSeleccionada = findNavController()
            .getBackStackEntry(R.id.subrazaFragment)
            .savedStateHandle
            .get<String>("subrazaSeleccionada")

        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarAtributos)

        btnConfirmar.setOnClickListener {

            // 1) Guardar raza + subraza en ViewModel
            personajeVM.setRazaSubraza(razaSeleccionada, subrazaSeleccionada)

            // 2) Guardar atributos finales (tu mapa ya existe: atributosFinales)
            personajeVM.setAtributosFinales(atributosFinales)

            // 3) Volver directamente a CrearPersonajeFragment
            findNavController().popBackStack(R.id.crearPersonajeFragment, false)
        }


    }

    private fun calcularModificador(total: Int): Int {
        return when (total) {
            in 1..9 -> -1
            in 10..11 -> 0
            in 12..13 -> 1
            in 14..15 -> 2
            in 16..17 -> 3
            in 18..19 -> 4
            else -> 5
        }
    }

    private fun configurarFila(
        row: View,
        nombre: String,
        bonus: Int,
        onPuntosCambiados: () -> Unit
    ) {
        val tvNombre = row.findViewById<TextView>(R.id.tvNombreAtributo)
        val tvValor = row.findViewById<TextView>(R.id.tvValor)
        val tvBonus = row.findViewById<TextView>(R.id.tvBonus)
        val tvTotal = row.findViewById<TextView>(R.id.tvTotal)
        val tvMod = row.findViewById<TextView>(R.id.tvModificador)
        val btnMinus = row.findViewById<Button>(R.id.btnMinus)
        val btnPlus = row.findViewById<Button>(R.id.btnPlus)


        tvNombre.text = nombre

        var valorBase = 10

        fun refrescar() {
            val total = valorBase + bonus
            val mod = calcularModificador(total)

            tvValor.text = valorBase.toString()
            tvBonus.text = if (bonus >= 0) "+$bonus" else bonus.toString()
            tvTotal.text = total.toString()
            tvMod.text = if (mod >= 0) "+$mod" else mod.toString()

            // 👉 Guardar el total final del atributo
            atributosFinales[nombre] = total
        }

        refrescar()

        btnPlus.setOnClickListener {
            if (puntosRestantes <= 0) return@setOnClickListener
            if (valorBase >= maxAtributo) return@setOnClickListener

            valorBase++
            puntosRestantes--

            refrescar()
            onPuntosCambiados()
        }

        btnMinus.setOnClickListener {
            if (valorBase <= minAtributo) return@setOnClickListener

            valorBase--
            puntosRestantes++

            refrescar()
            onPuntosCambiados()
        }


    }

    private fun calcularBonusRazaSubraza(raza: String, subraza: String): Map<String, Int> {
        val bonus = mutableMapOf(
            "Fuerza" to 0,
            "Destreza" to 0,
            "Constitución" to 0,
            "Inteligencia" to 0,
            "Sabiduría" to 0,
            "Carisma" to 0
        )

        // BONUS POR RAZA
        when (raza) {
            "Elfo" -> bonus["Destreza"] = bonus["Destreza"]!! + 2
            "Enano" -> bonus["Constitución"] = bonus["Constitución"]!! + 2
            "Humano" -> bonus["Carisma"] = bonus["Carisma"]!! + 1
        }

        // BONUS POR SUBRAZA
        when (subraza) {
            "Elfo del bosque" -> bonus["Sabiduría"] = bonus["Sabiduría"]!! + 1
            "Elfo lunar" -> bonus["Inteligencia"] = bonus["Inteligencia"]!! + 1
            "Enano de montaña" -> bonus["Fuerza"] = bonus["Fuerza"]!! + 1
        }

        return bonus
    }
}

