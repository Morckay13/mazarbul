package com.morcay.mazarbul.ui.creacion

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Diálogo con selección múltiple de habilidades.
 *
 * - Muestra una lista con checkboxes
 * - Permite seleccionar hasta "maxSeleccion"
 * - Devuelve la lista seleccionada mediante un callback (lambda)
 */
class HabilidadesDialogFragment(
    private val habilidades: Array<String>,
    private val maxSeleccion: Int,
    private val seleccionInicial: BooleanArray,
    private val onConfirmar: (List<String>) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Copia local para ir marcando/desmarcando
        val checked = seleccionInicial.copyOf()
        var contador = checked.count { it }

        return AlertDialog.Builder(requireContext())
            .setTitle("Selecciona habilidades (máx $maxSeleccion)")
            .setMultiChoiceItems(habilidades, checked) { _, which, isChecked ->

                if (isChecked) {
                    // Si intenta marcar y ya está al límite, lo desmarcamos
                    if (contador >= maxSeleccion) {
                        // Revertimos el check
                        checked[which] = false
                        // Esto evita que quede marcado visualmente
                        (dialog as? AlertDialog)?.listView?.setItemChecked(which, false)
                    } else {
                        checked[which] = true
                        contador++
                    }
                } else {
                    // Desmarcar
                    if (checked[which]) {
                        checked[which] = false
                        contador--
                    }
                }
            }
            .setPositiveButton("Confirmar") { _, _ ->
                // Construimos lista final seleccionada
                val seleccionadas = habilidades.filterIndexed { index, _ -> checked[index] }
                onConfirmar(seleccionadas)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}
