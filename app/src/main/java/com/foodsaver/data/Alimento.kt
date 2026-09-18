package com.foodsaver.data

data class Alimento(
    val id: Int,
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
 * Estado compartido de la app en memoria (equivalente a los useState del
 * componente App.tsx original). No usa Room porque, igual que en el
 * prototipo, los alimentos y la sesión no son parte del requisito de
 * persistencia: solo la entidad User se guarda en la base de datos local.
 */
object AppSession {
    var currentUser: AuthUser? = null
    val authUsers = mutableListOf<AuthUser>()

    var nextFoodId = 1
    val alimentos = mutableListOf<Alimento>()

    val CATEGORIAS = listOf("Lácteos", "Frutas", "Verduras", "Carnes", "Granos", "Otros")

    fun urgentes(): List<Alimento> = alimentos.filter { it.dias <= 5 }
    fun soloUrgentes(): List<Alimento> = alimentos.filter { it.dias <= 2 }

    fun recetasSugeridas(): List<Triple<String, String, String>> {
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
