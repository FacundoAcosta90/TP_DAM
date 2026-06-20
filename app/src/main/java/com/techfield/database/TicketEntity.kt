package com.techfield.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val ubicacion: String,
    val estado: String, // "Nuevo", "En Curso", "Pendiente", "Finalizado"
    val prioridad: String, // "Alta", "Media", "Baja"
    val fotoUri: String? = null,

    // --- NUEVOS CAMPOS PARA EL SLA ---
    val fechaCreacion: Long = System.currentTimeMillis(),
    val ultimaVezPausado: Long? = null,
    val tiempoPausadoAcumulado: Long = 0L
)