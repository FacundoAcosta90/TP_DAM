package com.techfield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.techfield.data.local.ComentarioEntity
import com.techfield.database.TicketEntity
import com.techfield.database.UserEntity
import com.techfield.repository.TicketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    val tickets = repository
        .getAllTickets()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun agregarTicket(ticket: TicketEntity) {
        viewModelScope.launch {
            repository.insert(ticket)
        }
    }

    fun actualizarTicket(ticket: TicketEntity) {
        viewModelScope.launch {
            repository.update(ticket)
        }
    }

    fun eliminarTicket(ticket: TicketEntity) {
        viewModelScope.launch {
            repository.delete(ticket)
        }
    }


    suspend fun autenticarUsuario(usuarioName: String): UserEntity? {
        return repository.obtenerUsuario(usuarioName)
    }


    fun agregarComentario(ticketId: Int, texto: String) {
        viewModelScope.launch {
            if (texto.isNotBlank()) {
                val nuevoComentario = ComentarioEntity(ticketId = ticketId, texto = texto)
                repository.insertComentario(nuevoComentario)
            }
        }
    }

    fun obtenerComentarios(ticketId: Int): kotlinx.coroutines.flow.Flow<List<ComentarioEntity>> {
        return repository.getComentariosPorTicket(ticketId)
    }
}

class TicketViewModelFactory(
    private val repository: TicketRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return TicketViewModel(repository) as T
    }
}