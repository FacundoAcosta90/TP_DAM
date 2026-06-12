Kotlin
package com.techfield.app.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import com.techfield.app.data.local.entities.ClienteEntity

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes")
    suspend fun getAllClientes(): List<ClienteEntity>
}