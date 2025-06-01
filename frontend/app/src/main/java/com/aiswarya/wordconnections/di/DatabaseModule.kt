package com.aiswarya.wordconnections.di

import android.content.Context
import androidx.room.Room
import com.aiswarya.wordconnections.data.local.database.PuzzleDatabase
import com.aiswarya.wordconnections.data.local.dao.PuzzleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePuzzleDatabase(
        @ApplicationContext context: Context
    ): PuzzleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PuzzleDatabase::class.java,
            PuzzleDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Remove in production
            .build()
    }

    @Provides
    fun providePuzzleDao(database: PuzzleDatabase): PuzzleDao {
        return database.puzzleDao()
    }
}