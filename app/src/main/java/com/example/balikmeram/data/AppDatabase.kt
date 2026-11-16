package com.example.balikmeram.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [MeraEntity::class, AvEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun meraDao(): MeraDao
    abstract fun avDao(): AvDao // Yeni Dao'muz

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "balik_meram_db"
                )
                    .fallbackToDestructiveMigration() // Versiyon artınca eski verileri silip yeniyi kurar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}