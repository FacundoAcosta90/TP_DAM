package com.techfield.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "comentarios")
data class ComentarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: Int,
    val texto: String,
    val fechaHora: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
)