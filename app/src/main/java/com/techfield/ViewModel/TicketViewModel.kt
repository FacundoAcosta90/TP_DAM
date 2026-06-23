package com.techfield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techfield.database.TicketEntity
import com.techfield.database.UserEntity
import com.techfield.database.RepuestoEntity
import com.techfield.data.local.ComentarioEntity
import com.techfield.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    companion object {
        private val _usuarioLogueado = MutableStateFlow<UserEntity?>(null)
        val sharedUsuarioLogueado = _usuarioLogueado.asStateFlow()
    }

    val usuarioLogueado = sharedUsuarioLogueado

    val tickets = repository
        .getAllTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setUsuarioLogueado(user: UserEntity?) {
        _usuarioLogueado.value = user
    }

    fun agregarTicket(ticket: TicketEntity) {
        viewModelScope.launch { repository.insertarTicket(ticket) }
    }

    fun actualizarTicket(ticket: TicketEntity) {
        viewModelScope.launch { repository.update(ticket) }
    }

    fun eliminarTicket(ticket: TicketEntity) {
        viewModelScope.launch { repository.delete(ticket) }
    }

    suspend fun autenticarUsuario(usuarioName: String): UserEntity? {
        return repository.obtenerUsuario(usuarioName)
    }

    fun obtenerRepuestos(): Flow<List<RepuestoEntity>> {
        return repository.obtenerTodosLosRepuestos()
    }

    fun descontarStockRepuesto(id: String, cantidad: Int) {
        viewModelScope.launch { repository.descontarStock(id, cantidad) }
    }

    fun obtenerComentarios(ticketId: Int): Flow<List<ComentarioEntity>> {
        return repository.obtenerComentarios(ticketId)
    }


    fun agregarComentario(comentario: ComentarioEntity) {
        viewModelScope.launch { repository.insertarComentario(comentario) }
    }


    fun agregarComentario(ticketId: Int, texto: String) {
        viewModelScope.launch {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            repository.insertarComentario(
                ComentarioEntity(
                    ticketId = ticketId,
                    texto = texto,
                    fechaHora = fechaActual
                )
            )
        }
    }


    fun calcularVencimientoSLA(timestampInicio: Long, limiteHoras: Int = 72): Boolean {
        if (timestampInicio == 0L) return false
        return (System.currentTimeMillis() - timestampInicio) > (limiteHoras * 60 * 60 * 1000L)
    }

    fun obtenerCuentaRegresivaSLA(timestampInicio: Long): String {
        if (timestampInicio == 0L) return "72:00:00"
        val tiempoRestante = (72 * 60 * 60 * 1000L) - (System.currentTimeMillis() - timestampInicio)
        if (tiempoRestante <= 0) return "00:00:00"
        val horas = tiempoRestante / (1000 * 60 * 60)
        val minutos = (tiempoRestante % (1000 * 60 * 60)) / (1000 * 60)
        val segundos = (tiempoRestante % (1000 * 60)) / 1000
        return String.format("%02d:%02d:%02d", horas, minutos, segundos)
    }
}