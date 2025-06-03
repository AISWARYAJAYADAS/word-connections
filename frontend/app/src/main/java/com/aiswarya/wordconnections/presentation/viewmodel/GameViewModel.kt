package com.aiswarya.wordconnections.presentation.viewmodel

import android.os.Build
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.model.ValidationResult
import com.aiswarya.wordconnections.domain.usecase.GetPuzzleUseCase
import com.aiswarya.wordconnections.domain.usecase.ValidateGuessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getPuzzleUseCase: GetPuzzleUseCase,
    private val validateGuessUseCase: ValidateGuessUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        // Restore puzzlesSolved and last reset date
        val puzzlesSolved = savedStateHandle.get<Int>("puzzlesSolved") ?: 0
        val lastResetDate = savedStateHandle.get<String>("lastResetDate") ?: ""
        val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Reset puzzlesSolved daily
        if (lastResetDate != today) {
            savedStateHandle["puzzlesSolved"] = 0
            savedStateHandle["lastResetDate"] = today
            _uiState.update { it.copy(puzzlesSolved = 0) }
        } else {
            _uiState.update { it.copy(puzzlesSolved = puzzlesSolved) }
        }

        loadPuzzle()
    }

    fun loadPuzzle(seed: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPuzzleUseCase(seed).onSuccess { puzzle ->
                _uiState.update {
                    it.copy(
                        puzzle = puzzle,
                        isLoading = false,
                        errorMessage = null,
                        gameStatus = GameStatus.PLAYING,
                        remainingAttempts = 4
                    )
                }
            }.onFailure { error ->
                Log.e("GameViewModel", "Error loading puzzle", error)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load puzzle"
                    )
                }
            }
        }
    }

    fun toggleWordSelection(word: String) {
        if (_uiState.value.gameStatus != GameStatus.PLAYING) return
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedWords.contains(word)) {
                currentState.selectedWords - word
            } else if (currentState.selectedWords.size < 4) {
                currentState.selectedWords + word
            } else {
                currentState.selectedWords.drop(1).toSet() + word
            }
            currentState.copy(selectedWords = newSelection)
        }
    }

    fun submitGuess() {
        val currentState = _uiState.value
        if (currentState.selectedWords.size != 4 ||
            currentState.puzzle == null ||
            currentState.gameStatus != GameStatus.PLAYING
        ) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            validateGuessUseCase(
                currentState.puzzle.puzzleId,
                currentState.selectedWords.toList()
            ).onSuccess { result ->
                handleValidationResult(result)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to validate guess"
                    )
                }
            }
        }
    }

    private fun handleValidationResult(result: ValidationResult) {
        _uiState.update { currentState ->
            val newState = if (result.isCorrect) {
                val solvedGroup = currentState.puzzle?.groups?.find { group ->
                    result.category?.let { category -> group.theme == category } ?: false
                }
                val newSolvedGroups = currentState.solvedGroups + listOfNotNull(solvedGroup)
                val isGameWon = newSolvedGroups.size == 4
                val newPuzzlesSolved = if (isGameWon) currentState.puzzlesSolved + 1 else currentState.puzzlesSolved
                if (isGameWon) {
                    savedStateHandle["puzzlesSolved"] = newPuzzlesSolved // Persist puzzlesSolved
                }
                currentState.copy(
                    selectedWords = emptySet(),
                    solvedGroups = newSolvedGroups,
                    remainingAttempts = result.remainingAttempts,
                    gameStatus = if (isGameWon) GameStatus.WON else GameStatus.PLAYING,
                    isLoading = false,
                    puzzlesSolved = newPuzzlesSolved
                )
            } else {
                val isGameLost = result.remainingAttempts <= 0
                currentState.copy(
                    selectedWords = emptySet(),
                    remainingAttempts = result.remainingAttempts,
                    gameStatus = if (isGameLost) GameStatus.LOST else GameStatus.PLAYING,
                    isLoading = false
                )
            }
            newState
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedWords = emptySet()) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun shuffleWords() {
        _uiState.update { currentState ->
            currentState.puzzle?.let { puzzle ->
                val solvedWords = currentState.solvedGroups.flatMap { it.words }.toSet()
                val availableWords = puzzle.words.filterNot { it in solvedWords }
                val shuffledWords = availableWords.shuffled()
                val updatedWords = (shuffledWords + solvedWords).distinct()
                val shuffledPuzzle = puzzle.copy(words = updatedWords)
                currentState.copy(puzzle = shuffledPuzzle)
            } ?: currentState
        }
    }

    fun restartGame() {
        _uiState.update { GameUiState(puzzlesSolved = _uiState.value.puzzlesSolved) }
        loadPuzzle()
    }

    fun newGame(seed: Int? = null) {
        _uiState.update { GameUiState(puzzlesSolved = _uiState.value.puzzlesSolved) }
        loadPuzzle(seed)
    }
}

