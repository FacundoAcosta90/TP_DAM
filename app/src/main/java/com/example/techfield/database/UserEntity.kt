package com.example.techfield.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val usuario: String, // El nombre de usuario será la clave única
    val contrasenia: String,
    val nombreCompleto: String,
    val especialidad: String
)