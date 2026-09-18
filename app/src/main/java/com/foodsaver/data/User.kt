package com.foodsaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad (Model) que representa un usuario registrado en la base de datos
 * local de la app. Cada campo del formulario "Registrar usuario" de la
 * pantalla Usuarios se corresponde 1 a 1 con una propiedad de esta clase.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val direccion: String,
    val telefono: String
)
