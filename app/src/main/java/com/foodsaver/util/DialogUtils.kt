package com.foodsaver.util

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * Diálogo de confirmación reutilizable para cualquier acción que convenga
 * confirmar antes de ejecutar (eliminar, cerrar sesión, etc.).
 */
object DialogUtils {

    fun confirmar(
        context: Context,
        titulo: String,
        mensaje: String,
        textoConfirmar: String = "Sí, continuar",
        textoCancelar: String = "Cancelar",
        onConfirmar: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(textoConfirmar) { dialog, _ ->
                dialog.dismiss()
                onConfirmar()
            }
            .setNegativeButton(textoCancelar) { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }
}
