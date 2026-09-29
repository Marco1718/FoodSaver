package com.foodsaver.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database Class: base de datos local de la app, implementada con Room.
 * Sigue el patrón Singleton para garantizar que exista una única
 * instancia de la base de datos durante todo el ciclo de vida de la app,
 * evitando abrir varias conexiones a la vez.
 */
@Database(entities = [User::class, Alimento::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun alimentoDao(): AlimentoDao

    companion object {
        // @Volatile garantiza que los cambios sobre INSTANCE sean visibles
        // inmediatamente para todos los hilos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si ya existe una instancia, la devolvemos; si no, la creamos
            // dentro de un bloque sincronizado para evitar condiciones de
            // carrera si dos hilos piden la instancia al mismo tiempo.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodsaver_database"
                )
                    // Se agregó la tabla "alimentos" (versión 2). No hay datos
                    // productivos que migrar, así que en vez de escribir un
                    // Migration a mano, dejamos que Room recree la base si
                    // encuentra una versión anterior en el dispositivo.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
