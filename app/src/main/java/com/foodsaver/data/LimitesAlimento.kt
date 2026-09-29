package com.foodsaver.data

/**
 * Límites razonables para evitar datos absurdos al registrar un alimento
 * (p. ej. "5000 de leche" que caduca en "1983 días"). Los días máximos
 * varían por categoría porque no todo caduca igual: un lácteo fresco no
 * aguanta meses, pero un grano seco sí.
 *
 * Son valores de sentido común para una app doméstica, no datos de una
 * norma sanitaria — se pueden ajustar aquí sin tocar el resto del código.
 */
object LimitesAlimento {

    /** Tope de unidades para cualquier alimento (nadie guarda miles de algo en casa). */
    const val CANTIDAD_MINIMA = 1
    const val CANTIDAD_MAXIMA = 500

    private val diasMaximosPorCategoria = mapOf(
        "Lácteos" to 60,   // leche, yogurt, quesos frescos (deja margen para quesos curados)
        "Frutas" to 45,    // fruta fresca; incluso la más resistente rara vez pasa de 1-2 meses
        "Verduras" to 45,  // igual que frutas; papa/zanahoria son la excepción, no la regla
        "Carnes" to 180,   // asumiendo que puede estar congelada (~6 meses)
        "Granos" to 365,   // arroz, pasta, cereales secos: hasta 1 año
        "Otros" to 180
    )

    private const val DIAS_MAXIMO_DEFECTO = 180

    fun diasMaximoPara(categoria: String): Int =
        diasMaximosPorCategoria[categoria] ?: DIAS_MAXIMO_DEFECTO
}
