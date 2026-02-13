package com.morcay.mazarbul.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonajeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(personaje: PersonajeEntity)

    @Query("SELECT * FROM personajes ORDER BY id DESC")
    suspend fun getAll(): List<PersonajeEntity>
}
