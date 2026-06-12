Kotlin
package com.techfield.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "tecnicos")
data class TecnicoEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "nombre_completo") val nombreCompleto: String,
    val usuario: String,
    val password: String,
    val rol: String
)