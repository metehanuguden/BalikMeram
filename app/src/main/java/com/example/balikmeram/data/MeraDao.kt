package com.example.balikmeram.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeraDao {
    @Query("SELECT * FROM meralar ORDER BY id DESC")
    fun getAllMeralar(): Flow<List<MeraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMera(mera: MeraEntity)

    @Query("DELETE FROM meralar WHERE id = :id")
    suspend fun deleteMeraById(id: Int)
}