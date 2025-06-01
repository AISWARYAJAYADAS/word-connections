package com.aiswarya.wordconnections.di

import com.aiswarya.wordconnections.domain.repository.PuzzleRepository
import com.aiswarya.wordconnections.domain.usecase.GetPuzzleUseCase
import com.aiswarya.wordconnections.domain.usecase.ValidateGuessUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetPuzzleUseCase(
        puzzleRepository: PuzzleRepository
    ): GetPuzzleUseCase {
        return GetPuzzleUseCase(puzzleRepository)
    }

    @Provides
    @Singleton
    fun provideValidateGuessUseCase(
        puzzleRepository: PuzzleRepository
    ): ValidateGuessUseCase {
        return ValidateGuessUseCase(puzzleRepository)
    }
}
