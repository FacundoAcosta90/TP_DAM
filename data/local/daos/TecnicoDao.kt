Kotlin
package com.techfield.app.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import com.techfield.app.data.local.entities.TecnicoEntity

@Dao
interface TecnicoDao {
    @Query("SELECT * FROM tecnicos WHERE usuario = :usuario LIMIT 1")
    suspend fun getTecnicoByUsuario(usuario: String): TecnicoEntity?
}
