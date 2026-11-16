package com.example.balikmeram.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avlar")
data class AvEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val balikTuru: String,
    val agirlik: String,
    val ekipman: String,
    val imageUri: String? = null // FOTOĞRAF İÇİN YENİ EKLENDİ
)