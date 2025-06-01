package com.aiswarya.wordconnections.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.aiswarya.wordconnections.data.local.dao.PuzzleDao
import com.aiswarya.wordconnections.data.local.entity.PuzzleEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleGroupEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleWordEntity

@Database(
    entities = [
        PuzzleEntity::class,
        PuzzleGroupEntity::class,
        PuzzleWordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PuzzleDatabase : RoomDatabase() {

    abstract fun puzzleDao(): PuzzleDao

    companion object {
        const val DATABASE_NAME = "puzzle_database"

        @Volatile
        private var INSTANCE: PuzzleDatabase? = null

        fun getDatabase(context: Context): PuzzleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PuzzleDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // Remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}