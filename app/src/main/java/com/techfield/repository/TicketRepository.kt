package com.techfield.repository

import com.techfield.database.TicketDao
import com.techfield.database.TicketEntity
import com.techfield.database.UserEntity
import com.techfield.database.RepuestoEntity
import com.techfield.data.local.ComentarioEntity
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {

    fun getAllTickets(): Flow<List<TicketEntity>> = ticketDao.getAllTickets()

    suspend fun insertarTicket(ticket: TicketEntity) = ticketDao.insertarTicket(ticket)

    suspend fun update(ticket: TicketEntity) = ticketDao.update(ticket)

    suspend fun delete(ticket: TicketEntity) = ticketDao.delete(ticket)

    suspend fun obtenerUsuario(usuarioName: String): UserEntity? = ticketDao.obtenerUsuario(usuarioName)

    fun obtenerTodosLosRepuestos(): Flow<List<RepuestoEntity>> = ticketDao.obtenerTodosLosRepuestos()

    suspend fun descontarStock(id: String, cantidad: Int) = ticketDao.descontarStock(id, cantidad)

    fun obtenerComentarios(ticketId: Int): Flow<List<ComentarioEntity>> = ticketDao.obtenerComentariosPorTicket(ticketId)

    suspend fun insertarComentario(comentario: ComentarioEntity) = ticketDao.insertarComentario(comentario)
}