package com.example.techfield.database

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
    val fotoUri: String? = null
)