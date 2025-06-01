package com.aiswarya.wordconnections.domain.usecase

import com.aiswarya.wordconnections.domain.model.ValidationResult
import com.aiswarya.wordconnections.domain.repository.PuzzleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateGuessUseCase @Inject constructor(
    private val repository: PuzzleRepository
) {
    suspend operator fun invoke(
        puzzleId: String,
        selectedWords: List<String>
    ): Result<ValidationResult> {
        return repository.validateGuess(puzzleId, selectedWords)
    }
}