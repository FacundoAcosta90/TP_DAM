Kotlin
package com.techfield.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(
    tableName = "evidencias_fotograficas",
    foreignKeys = [
        ForeignKey(
            entity = TicketEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticket_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EvidenciaFotograficaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "ticket_id") val ticketId: Int,
    @ColumnInfo(name = "foto_uri") val fotoUri: String
)