package com.morcay.mazarbul.ui.creacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonajeViewModel : ViewModel() {

    // =========================
    // ESTADO / MODO DE SESIÓN
    // =========================
    private enum class Mode { IDLE, CREATING, EDITING }

    private var mode: Mode = Mode.IDLE
    private var editingId: Int? = null

    // =========================
    // RAZA / SUBRAZA
    // =========================
    private val _raza = MutableLiveData<String?>(null)
    val raza: LiveData<String?> = _raza

    private val _subraza = MutableLiveData<String?>(null)
    val subraza: LiveData<String?> = _subraza

    // =========================
    // ATRIBUTOS
    // =========================
    private val _atributosFinales = MutableLiveData<Map<String, Int>>(emptyMap())
    val atributosFinales: LiveData<Map<String, Int>> = _atributosFinales

    // =========================
    // CLASE / SUBCLASE / HABILIDADES
    // =========================
    private val _clase = MutableLiveData<String?>(null)
    val clase: LiveData<String?> = _clase

    private val _subclase = MutableLiveData<String?>(null)
    val subclase: LiveData<String?> = _subclase

    private val _habilidades = MutableLiveData<List<String>>(emptyList())
    val habilidades: LiveData<List<String>> = _habilidades

    // =========================
    // INVENTARIO MÍNIMO (CA)
    // =========================
    private val _armaduraEquipada = MutableLiveData("Sin armadura")
    val armaduraEquipada: LiveData<String> = _armaduraEquipada

    private val _tieneEscudo = MutableLiveData(false)
    val tieneEscudo: LiveData<Boolean> = _tieneEscudo

    // ------------------------------------------------------------
    // SESIÓN: CREAR NUEVO
    // ------------------------------------------------------------
    /**
     * Llamar SOLO al entrar a CrearPersonajeFragment en modo CREACIÓN.
     * Resetea UNA sola vez por sesión (para que al volver de selectores no se borre).
     */
    fun startNewDraft() {
        if (mode == Mode.CREATING) return

        mode = Mode.CREATING
        editingId = null

        // Reset del borrador
        _raza.value = null
        _subraza.value = null
        _atributosFinales.value = emptyMap()

        _clase.value = null
        _subclase.value = null
        _habilidades.value = emptyList()

        // Inventario mínimo
        _armaduraEquipada.value = "Sin armadura"
        _tieneEscudo.value = false
    }

    // ------------------------------------------------------------
    // SESIÓN: EDITAR EXISTENTE
    // ------------------------------------------------------------
    /**
     * Llamar al entrar a CrearPersonajeFragment en modo EDICIÓN.
     * No resetea nada; solo marca modo edición.
     */
    fun startEditing(personajeId: Int) {
        mode = Mode.EDITING
        editingId = personajeId
    }

    /**
     * Cuando guardas o cancelas, dejamos el VM listo para una nueva sesión limpia.
     */
    fun finishDraft() {
        mode = Mode.IDLE
        editingId = null
    }

    // ------------------------------------------------------------
    // SETTERS (los que ya usas)
    // ------------------------------------------------------------
    fun setRazaSubraza(raza: String?, subraza: String?) {
        _raza.value = raza
        _subraza.value = subraza
    }

    fun setAtributosFinales(atributos: Map<String, Int>) {
        _atributosFinales.value = atributos
    }

    fun setClaseCompleta(clase: String?, subclase: String?, habilidades: List<String>) {
        _clase.value = clase
        _subclase.value = subclase
        _habilidades.value = habilidades
    }

    // ------------------------------------------------------------
    // INVENTARIO MÍNIMO (por si luego lo usas en creación también)
    // ------------------------------------------------------------
    fun setArmadura(nombre: String) {
        _armaduraEquipada.value = nombre
    }

    fun setEscudo(tiene: Boolean) {
        _tieneEscudo.value = tiene
    }
}



