package com.example.balikmeram.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meralar")
data class MeraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)