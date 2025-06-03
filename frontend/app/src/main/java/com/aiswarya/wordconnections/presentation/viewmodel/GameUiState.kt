package com.aiswarya.wordconnections.presentation.viewmodel

import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.model.PuzzleGroup

data class GameUiState(
    val puzzle: Puzzle? = null,
    val selectedWords: Set<String> = emptySet(),
    val solvedGroups: List<PuzzleGroup> = emptyList(),
    val remainingAttempts: Int = 4,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val puzzlesSolved: Int = 0
)

enum class GameStatus {
    PLAYING, WON, LOST
}