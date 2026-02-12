package com.morcay.mazarbul.ui.creacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonajeViewModel : ViewModel() {

    // Raza
    private val _raza = MutableLiveData<String?>(null)
    val raza: LiveData<String?> = _raza

    // Subraza
    private val _subraza = MutableLiveData<String?>(null)
    val subraza: LiveData<String?> = _subraza

    // Atributos finales (totales)
    private val _atributosFinales = MutableLiveData<Map<String, Int>>(emptyMap())
    val atributosFinales: LiveData<Map<String, Int>> = _atributosFinales

    fun setRazaSubraza(raza: String?, subraza: String?) {
        _raza.value = raza
        _subraza.value = subraza
    }

    fun setAtributosFinales(atributos: Map<String, Int>) {
        _atributosFinales.value = atributos
    }
}

