package com.techfield.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val ubicacion: String,
    val estado: String,
    val prioridad: String,
    val fotoUri: String? = null,
    val timestampInicio: Long = 0L,
    val fechaInicioFormat: String? = null,
    val observacionesTecnicas: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val ultimaVezPausado: Long? = null,
    val tiempoPausadoAcumulado: Long = 0L
)