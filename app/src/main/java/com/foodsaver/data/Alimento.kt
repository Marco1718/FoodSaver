package com.foodsaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room de un alimento. "ownerEmail" es el correo de la cuenta
 * (AuthUser) dueña del registro: así cada usuario solo ve y modifica sus
 * propios alimentos, aunque estén en la misma tabla/base de datos.
 */
@Entity(tableName = "alimentos")
data class Alimento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ownerEmail: String,
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val dias: Int
)

data class AuthUser(
    val nombre: String,
    val email: String,
    val password: String
)

enum class Estado(val texto: String) { URGENTE("URGENTE"), PROXIMO("PRÓXIMO"), NORMAL("NORMAL") }

fun estadoDe(dias: Int): Estado = when {
    dias <= 2 -> Estado.URGENTE
    dias <= 5 -> Estado.PROXIMO
    else -> Estado.NORMAL
}

/**
 * Estado de sesión en memoria (login actual + cuentas registradas). Las
 * cuentas (AuthUser) siguen en memoria, como antes; lo que cambió es que
 * los ALIMENTOS de cada cuenta ahora viven en Room (tabla "alimentos"),
 * filtrados por el correo de quien inició sesión — ver AlimentoDao.
 */
object AppSession {
    var currentUser: AuthUser? = null
    val authUsers = mutableListOf<AuthUser>()

    val CATEGORIAS = listOf("Lácteos", "Frutas", "Verduras", "Carnes", "Granos", "Otros")

    /** Alimentos con 5 días o menos, ordenados: el más urgente (menos días) primero. */
    fun urgentes(alimentos: List<Alimento>): List<Alimento> =
        alimentos.filter { it.dias <= 5 }.sortedBy { it.dias }

    fun soloUrgentes(alimentos: List<Alimento>): List<Alimento> = alimentos.filter { it.dias <= 2 }

    fun recetasSugeridas(alimentos: List<Alimento>): List<Triple<String, String, String>> {
        val nombres = alimentos.map { it.nombre.lowercase() }
        val out = mutableListOf<Triple<String, String, String>>()
        if (nombres.contains("leche") && nombres.contains("tomate"))
            out.add(Triple("Crema de tomate", "Utiliza leche y tomate para una crema sencilla y reconfortante.", "Sopa"))
        if (nombres.contains("huevo") && nombres.contains("tomate") && nombres.contains("queso"))
            out.add(Triple("Omelette de tomate y queso", "Combina huevo, tomate y queso para aprovechar varios ingredientes.", "Desayuno"))
        if (nombres.contains("tortilla") && nombres.contains("queso"))
            out.add(Triple("Quesadillas", "Una opción rápida para aprovechar tortillas y queso.", "Snack"))
        return out
    }

    fun cerrarSesion() {
        currentUser = null
    }
}
