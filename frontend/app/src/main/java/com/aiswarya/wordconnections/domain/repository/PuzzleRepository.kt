package com.aiswarya.wordconnections.domain.repository

import com.aiswarya.wordconnections.domain.model.GameProgress
import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.model.ValidationResult

interface PuzzleRepository {
    suspend fun getPuzzle(seed: Int? = null): Result<Puzzle>
    suspend fun getEnhancedPuzzle(seed: Int? = null): Result<Puzzle>
    suspend fun validateGuess(puzzleId: String, words: List<String>): Result<ValidationResult>
    suspend fun savePuzzleProgress(puzzleId: String, progress: GameProgress)
    suspend fun getPuzzleProgress(puzzleId: String): GameProgress?
}