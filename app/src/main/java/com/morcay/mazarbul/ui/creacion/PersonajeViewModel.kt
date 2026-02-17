package com.morcay.mazarbul.ui.creacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonajeViewModel : ViewModel() {

    // ===== RAZA / SUBRAZA =====
    private val _raza = MutableLiveData<String?>(null)
    val raza: LiveData<String?> = _raza

    private val _subraza = MutableLiveData<String?>(null)
    val subraza: LiveData<String?> = _subraza

    // ===== ATRIBUTOS FINALES (TOTALES) =====
    private val _atributosFinales = MutableLiveData<Map<String, Int>>(emptyMap())
    val atributosFinales: LiveData<Map<String, Int>> = _atributosFinales

    fun setRazaSubraza(raza: String?, subraza: String?) {
        _raza.value = raza
        _subraza.value = subraza
    }

    fun setAtributosFinales(atributos: Map<String, Int>) {
        _atributosFinales.value = atributos
    }

    // ===== CLASE / SUBCLASE / HABILIDADES =====
    private val _clase = MutableLiveData<String?>(null)
    val clase: LiveData<String?> = _clase

    private val _subclase = MutableLiveData<String?>(null)
    val subclase: LiveData<String?> = _subclase

    private val _habilidades = MutableLiveData<List<String>>(emptyList())
    val habilidades: LiveData<List<String>> = _habilidades

    fun setClaseCompleta(clase: String?, subclase: String?, habilidades: List<String>) {
        _clase.value = clase
        _subclase.value = subclase
        _habilidades.value = habilidades
    }

}


