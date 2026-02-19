package com.morcay.mazarbul.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonajeDao {

    @Insert
    suspend fun insert(personaje: PersonajeEntity)

    @Query("SELECT * FROM personajes ORDER BY id DESC")
    suspend fun getAll(): List<PersonajeEntity>

    @Query("SELECT * FROM personajes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PersonajeEntity?

    @Update
    suspend fun update(personaje: PersonajeEntity)

    @Delete
    suspend fun delete(personaje: PersonajeEntity)

    @Query("DELETE FROM personajes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE personajes SET armaduraEquipada = :armadura WHERE id = :id")
    suspend fun updateArmadura(id: Int, armadura: String)

    // ✅ NUEVO: guardar escudo
    @Query("UPDATE personajes SET tieneEscudo = :tieneEscudo WHERE id = :id")
    suspend fun updateEscudo(id: Int, tieneEscudo: Boolean)

}

