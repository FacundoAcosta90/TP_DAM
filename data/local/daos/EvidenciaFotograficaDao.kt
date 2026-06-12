Kotlin
package com.techfield.app.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techfield.app.data.local.entities.EvidenciaFotograficaEntity

@Dao
interface EvidenciaFotograficaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidencia(evidencia: EvidenciaFotograficaEntity)

    @Query("SELECT * FROM evidencias_fotograficas WHERE ticket_id = :ticketId")
    suspend fun getEvidenciasByTicket(ticketId: Int): List<EvidenciaFotograficaEntity>
}