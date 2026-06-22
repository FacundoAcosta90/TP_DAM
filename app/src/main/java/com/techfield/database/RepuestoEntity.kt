package com.techfield.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repuestos")
data class RepuestoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val stock: Int
)