package com.aiswarya.wordconnections.di

import com.aiswarya.wordconnections.data.repository.PuzzleRepositoryImpl
import com.aiswarya.wordconnections.domain.repository.PuzzleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPuzzleRepository(
        puzzleRepositoryImpl: PuzzleRepositoryImpl
    ): PuzzleRepository
}
