package com.techfield.repository

import com.techfield.database.TicketDao
import com.techfield.database.TicketEntity
import com.techfield.data.local.ComentarioEntity
import kotlinx.coroutines.flow.Flow

class TicketRepository(
    private val ticketDao: TicketDao
) {

    fun getAllTickets(): Flow<List<TicketEntity>> =
        ticketDao.getAllTickets()

    suspend fun getTicketById(id: Int): TicketEntity? =
        ticketDao.getTicketById(id)

    suspend fun insert(ticket: TicketEntity) =
        ticketDao.insert(ticket)

    suspend fun update(ticket: TicketEntity) =
        ticketDao.update(ticket)

    suspend fun delete(ticket: TicketEntity) =
        ticketDao.delete(ticket)



    suspend fun insertComentario(comentario: ComentarioEntity) {
        ticketDao.insertarComentario(comentario)
    }

    fun getComentariosPorTicket(ticketId: Int): Flow<List<ComentarioEntity>> {
        return ticketDao.obtenerComentariosPorTicket(ticketId)
    }


    suspend fun obtenerUsuario(usuario: String): com.techfield.database.UserEntity? {
        return ticketDao.obtenerUsuario(usuario)
    }
}