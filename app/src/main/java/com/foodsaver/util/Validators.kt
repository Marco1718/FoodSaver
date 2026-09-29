package com.foodsaver.util

import android.util.Patterns

/**
 * Reglas de validación compartidas por todos los formularios de la app
 * (login, registro, agregar/editar alimento, registrar usuario).
 * Centralizarlas aquí evita reglas distintas o inconsistentes entre pantallas.
 */
object Validators {

    /** Nombres/apellidos: no vacío, solo letras, espacios, acentos, apóstrofes y guiones. */
    fun esNombreValido(texto: String): Boolean =
        texto.trim().isNotEmpty() && texto.trim().matches(Regex("^[\\p{L}][\\p{L} .'-]{1,49}$"))

    /** Correo electrónico con formato válido. */
    fun esEmailValido(texto: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(texto.trim()).matches()

    /** Contraseña: al menos 6 caracteres. */
    fun esPasswordValida(texto: String): Boolean = texto.length >= 6

    /** Teléfono: solo dígitos, entre 7 y 15 (cubre formatos locales e internacionales). */
    fun esTelefonoValido(texto: String): Boolean =
        texto.trim().matches(Regex("^[0-9]{7,15}$"))

    /** Dirección: no vacía y con un mínimo de detalle. */
    fun esDireccionValida(texto: String): Boolean = texto.trim().length >= 5

    /** Entero estrictamente positivo (para cantidades), dentro del rango permitido. */
    fun esEnteroPositivo(texto: String): Boolean = texto.trim().toIntOrNull()?.let { it > 0 } ?: false

    /** Cantidad dentro del límite general de la app (evita cosas como "5000 de leche"). */
    fun esCantidadValida(texto: String): Boolean =
        texto.trim().toIntOrNull()?.let { it in com.foodsaver.data.LimitesAlimento.CANTIDAD_MINIMA..com.foodsaver.data.LimitesAlimento.CANTIDAD_MAXIMA } ?: false

    /** Entero mayor o igual a cero (para días para caducar). */
    fun esEnteroNoNegativo(texto: String): Boolean = texto.trim().toIntOrNull()?.let { it >= 0 } ?: false

    /** Días para caducar dentro del máximo razonable de ESA categoría (lácteos ≠ granos). */
    fun esDiasValidoParaCategoria(texto: String, categoria: String): Boolean {
        val dias = texto.trim().toIntOrNull() ?: return false
        return dias in 0..com.foodsaver.data.LimitesAlimento.diasMaximoPara(categoria)
    }

    /** Campo de texto genérico obligatorio. */
    fun noEsVacio(texto: String): Boolean = texto.trim().isNotEmpty()
}
