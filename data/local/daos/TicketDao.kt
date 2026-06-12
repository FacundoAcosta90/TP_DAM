Kotlin
package com.techfield.app.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techfield.app.data.local.entities.TicketEntity

@Dao
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Query("SELECT * FROM tickets")
    suspend fun getAllTickets(): List<TicketEntity>

    @Query("SELECT * FROM tickets WHERE tecnico_id = :tecnicoId")
    suspend fun getTicketsByTecnico(tecnicoId: Int): List<TicketEntity>
}