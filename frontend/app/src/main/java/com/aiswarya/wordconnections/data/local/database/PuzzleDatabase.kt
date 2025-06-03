package com.aiswarya.wordconnections.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
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
    }
}