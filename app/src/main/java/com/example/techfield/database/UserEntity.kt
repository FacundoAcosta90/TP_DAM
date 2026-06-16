package com.example.techfield.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val usuario: String,
    val contrasenia: String,
    val nombreCompleto: String,
    val especialidad: String
)