Kotlin
package com.techfield.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = MaquinaEntity::class,
            parentColumns = ["nro_de_serie"],
            childColumns = ["maquina_nro_serie"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TecnicoEntity::class,
            parentColumns = ["id"],
            childColumns = ["tecnico_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "maquina_nro_serie") val maquinaNroSerie: String,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: String,
    @ColumnInfo(name = "falla_reportada") val fallaReportada: String,
    val estado: String,
    @ColumnInfo(name = "tecnico_id") val tecnicoId: Int,
    val criticidad: String
)