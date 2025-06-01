package com.aiswarya.wordconnections.domain.usecase

import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.repository.PuzzleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPuzzleUseCase @Inject constructor(
    private val repository: PuzzleRepository
) {
    suspend operator fun invoke(seed: Int? = null): Result<Puzzle> {
        return repository.getEnhancedPuzzle(seed)
    }
}