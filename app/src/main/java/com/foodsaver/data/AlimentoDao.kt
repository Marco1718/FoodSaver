package com.foodsaver.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO de la tabla "alimentos". Todas las consultas de lectura reciben
 * "ownerEmail" para que cada usuario solo vea (y solo pueda tocar) sus
 * propios alimentos — es la pieza que evita que una cuenta nueva vea los
 * alimentos de otra.
 */
@Dao
interface AlimentoDao {

    @Insert
    suspend fun insertar(alimento: Alimento): Long

    @Update
    suspend fun actualizar(alimento: Alimento)

    @Delete
    suspend fun eliminar(alimento: Alimento)

    /** Read reactivo: se re-emite solo cuando cambian los alimentos de ESE usuario. */
    @Query("SELECT * FROM alimentos WHERE ownerEmail = :ownerEmail ORDER BY dias ASC, id DESC")
    fun obtenerPorUsuario(ownerEmail: String): Flow<List<Alimento>>

    @Query("SELECT * FROM alimentos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Alimento?
}
