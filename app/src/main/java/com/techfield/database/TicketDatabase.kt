package com.techfield.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techfield.data.local.ComentarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TicketEntity::class, UserEntity::class, ComentarioEntity::class, RepuestoEntity::class],
    version = 4,
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
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    val userDao = database.ticketDao()

                    // Pre-carga de Usuarios Oficiales
                    userDao.insertarUsuario(UserEntity("admin.it", "fieldit2026", "Juan", "IT"))
                    userDao.insertarUsuario(UserEntity("tecnico.field", "fieldtec2026", "Marcos Rodríguez", "Técnico"))

                    // Pre-carga del Stock de Repuestos Ajustado
                    val listadoRepuestos = listOf(
                        RepuestoEntity(id = "REP-01", nombre = "Fuente de energía", stock = 6),
                        RepuestoEntity(id = "REP-02", nombre = "Soporte", stock = 8),
                        RepuestoEntity(id = "REP-03", nombre = "Tornillos", stock = 20),
                        RepuestoEntity(id = "REP-04", nombre = "Cable hdmi", stock = 5),
                        RepuestoEntity(id = "REP-05", nombre = "Cable de red", stock = 3),
                        RepuestoEntity(id = "REP-06", nombre = "Pendrive SO", stock = 7)
                    )
                    listadoRepuestos.forEach { userDao.insertarRepuesto(it) }
                }
            }
        }
    }
}