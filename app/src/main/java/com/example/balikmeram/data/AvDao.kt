package com.example.balikmeram.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AvDao {
    @Query("SELECT * FROM avlar ORDER BY id DESC")
    fun getAllAvlar(): Flow<List<AvEntity>>

    @Insert
    suspend fun insertAv(av: AvEntity)

    @Delete
    suspend fun deleteAv(av: AvEntity)
}