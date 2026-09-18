package com.foodsaver.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): define las operaciones que la app puede
 * realizar sobre la tabla "users". Todas las funciones son `suspend`
 * (o devuelven un Flow) para poder ejecutarse fuera del hilo principal
 * mediante corrutinas.
 */
@Dao
interface UserDao {

    /** Inserta un nuevo registro. Si por alguna razón el id ya existe, lo reemplaza. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(user: User): Long

    /** Devuelve todos los usuarios, ordenados del más reciente al más antiguo. */
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<User>>

    /** Consulta puntual (no reactiva) de todos los usuarios. */
    @Query("SELECT * FROM users ORDER BY id DESC")
    suspend fun listarTodos(): List<User>

    /** Busca un usuario concreto por su id. */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun obtenerPorId(userId: Int): User?

    /** Número total de usuarios registrados. */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun contar(): Int
}
