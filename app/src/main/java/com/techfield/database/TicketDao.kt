package com.techfield.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.techfield.data.local.ComentarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Query("SELECT * FROM tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    suspend fun getTicketById(id: Int): TicketEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(ticket: TicketEntity)

    @Update
    suspend fun update(ticket: TicketEntity)

    @Delete
    suspend fun delete(ticket: TicketEntity)



    @Insert
    suspend fun insertarComentario(comentario: ComentarioEntity)

    @Query("SELECT * FROM comentarios WHERE ticketId = :ticketId ORDER BY id DESC")
    fun obtenerComentariosPorTicket(ticketId: Int): Flow<List<ComentarioEntity>>

    @Query("SELECT * FROM users WHERE usuario = :usuario LIMIT 1")
    suspend fun obtenerUsuario(usuario: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(user: UserEntity)
}