package com.techfield.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "maquinas")
data class MaquinaEntity(
    @PrimaryKey
    @ColumnInfo(name = "nro_de_serie")
    val nroDeSerie: String,

    @ColumnInfo(name = "articulo")
    val articulo: String,

    @ColumnInfo(name = "denominacion_comercial")
    val denominacionComercial: String,

    @ColumnInfo(name = "direccion")
    val direccion: String,

    @ColumnInfo(name = "sucursal")
    val sucursal: String,

    @ColumnInfo(name = "sector")
    val sector: String,

    @ColumnInfo(name = "observaciones")
    val observaciones: String
)