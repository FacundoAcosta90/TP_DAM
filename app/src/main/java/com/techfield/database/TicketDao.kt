package com.techfield.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.techfield.data.local.ComentarioEntity

@Dao
interface TicketDao {

    // --- Métodos de Tickets ---
    @Query("SELECT * FROM tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTicket(ticket: TicketEntity)

    @Update
    suspend fun update(ticket: TicketEntity)

    @Delete
    suspend fun delete(ticket: TicketEntity)

    // --- Métodos de Usuarios ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UserEntity)

    @Query("SELECT * FROM users WHERE usuario = :usuarioName LIMIT 1")
    suspend fun obtenerUsuario(usuarioName: String): UserEntity?

    // --- Métodos de Repuestos (Insumos) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRepuesto(repuesto: RepuestoEntity)

    @Query("SELECT * FROM repuestos")
    fun obtenerTodosLosRepuestos(): Flow<List<RepuestoEntity>>

    @Query("UPDATE repuestos SET stock = stock - :cantidad WHERE id = :id")
    suspend fun descontarStock(id: String, cantidad: Int)

    // --- Métodos de Comentarios ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarComentario(comentario: ComentarioEntity)

    @Query("SELECT * FROM comentarios WHERE ticketId = :ticketId ORDER BY id DESC")
    fun obtenerComentariosPorTicket(ticketId: Int): Flow<List<ComentarioEntity>>
}