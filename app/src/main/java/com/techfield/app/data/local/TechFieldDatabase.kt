package com.techfield.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techfield.app.data.local.daos.*
import com.techfield.app.data.local.entities.*

@Database(
    entities = [
        MaquinaEntity::class,
        //TicketEntity::class,
        //RepuestoUtilizadoEntity::class,
        //EvidenciaFotograficaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TechFieldDatabase : RoomDatabase() {

    abstract fun maquinaDao(): MaquinaDao
    //abstract fun ticketDao(): TicketDao
    //abstract fun repuestoUtilizadoDao(): RepuestoUtilizadoDao
    //abstract fun evidenciaFotograficaDao(): EvidenciaFotograficaDao

    companion object {
        @Volatile
        private var INSTANCE: TechFieldDatabase? = null

        fun getDatabase(context: Context): TechFieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TechFieldDatabase::class.java,
                    "techfield_database"
                )
                    .createFromAsset("database/techfield_database.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}