package com.techfield.app.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techfield.app.data.local.entities.MaquinaEntity

@Dao
interface MaquinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaquinas(maquinas: List<MaquinaEntity>)

    @Query("SELECT * FROM maquinas WHERE nro_de_serie = :nroDeSerie")
    suspend fun getMaquinaByNroSerie(nroDeSerie: String): MaquinaEntity?
}