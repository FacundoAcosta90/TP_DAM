package com.example.techfield.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techfield.data.local.ComentarioEntity // <-- NUEVO IMPORT: Traemos la entidad de comentarios
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Agregamos ComentarioEntity a la lista y subimos la versión a 3
@Database(
    entities = [TicketEntity::class, UserEntity::class, ComentarioEntity::class],
    version = 3,
    exportSchema = false
)
abstract class TicketDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: TicketDatabase? = null

        fun getDatabase(context: Context): TicketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TicketDatabase::class.java,
                    "ticket_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val userDao = database.ticketDao()
                        userDao.insertarUsuario(
                            UserEntity(
                                usuario = "marcos.tech",
                                contrasenia = "field2026",
                                nombreCompleto = "Marcos Rodríguez",
                                especialidad = "Especialista en Sistemas Críticos"
                            )
                        )
                    }
                }
            }
        }
    }
}