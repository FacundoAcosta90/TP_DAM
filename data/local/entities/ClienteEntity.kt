Kotlin
package com.techfield.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cliente_id") val clienteId: Int = 0,
    @ColumnInfo(name = "nombre_empresa") val nombreEmpresa: String,
    val cuit: String,
    val direccion: String,
    val sucursal: String,
    val sector: String,
    @ColumnInfo(name = "telefono_contacto") val telefonoContacto: String,
    @ColumnInfo(name = "email_soporte") val emailSoporte: String
)

----------------------------------------------------------
EvidenciaFotograficaEntity.kt

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