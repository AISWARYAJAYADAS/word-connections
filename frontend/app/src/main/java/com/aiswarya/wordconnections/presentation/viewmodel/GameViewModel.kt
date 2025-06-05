package com.aiswarya.wordconnections.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiswarya.wordconnections.domain.model.ValidationResult
import com.aiswarya.wordconnections.domain.usecase.GetPuzzleUseCase
import com.aiswarya.wordconnections.domain.usecase.ValidateGuessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
        // Restore state and reset daily
        val puzzlesSolved = savedStateHandle.get<Int>("puzzlesSolved") ?: 0
        val lastResetDate = savedStateHandle.get<String>("lastResetDate") ?: ""
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

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
            getPuzzleUseCase(seed, forceRefresh = true).fold(
                onSuccess = { puzzle ->
                    val puzzleSource = if (puzzle.puzzleId == "default_puzzle") {
                        PuzzleSource.HARDCODED
                    } else {
                        // Check if puzzle is from cache by attempting to fetch from network
                        try {
                            getPuzzleUseCase(seed, forceRefresh = true)
                            PuzzleSource.NETWORK
                        } catch (e: Exception) {
                            PuzzleSource.CACHE
                        }
                    }
                    _uiState.update {
                        it.copy(
                            puzzle = puzzle,
                            isLoading = false,
                            gameStatus = GameStatus.PLAYING,
                            remainingAttempts = 4,
                            puzzleSource = puzzleSource,
                            errorMessage = if (puzzleSource != PuzzleSource.NETWORK) {
                                when (puzzleSource) {
                                    PuzzleSource.CACHE -> "Playing offline with a cached puzzle."
                                    PuzzleSource.HARDCODED -> "Playing offline with a default puzzle."
                                    else -> null
                                }
                            } else null
                        )
                    }
                },
                onFailure = { error ->
                    Log.e("GameViewModel", "Error loading puzzle", error)
                    val errorMessage = when (error) {
                        is UnknownHostException, is ConnectException ->
                            "No internet connection. Please check your network."
                        else -> "Failed to load puzzle: ${error.message ?: "Unknown error"}"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    }
                }
            )
        }
    }

    fun toggleWordSelection(word: String) {
        if (_uiState.value.gameStatus != GameStatus.PLAYING) return
        _uiState.update { currentState ->
            val selectedWords = currentState.selectedWords
            val newSelection = when {
                selectedWords.contains(word) -> selectedWords - word
                selectedWords.size < 4 -> selectedWords + word
                else -> selectedWords.drop(1).toSet() + word
            }
            currentState.copy(selectedWords = newSelection)
        }
    }

    fun submitGuess() {
        val currentState = _uiState.value
        if (currentState.selectedWords.size != 4 || currentState.puzzle == null || currentState.gameStatus != GameStatus.PLAYING) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            validateGuessUseCase(
                currentState.puzzle.puzzleId,
                currentState.selectedWords.toList()
            ).fold(
                onSuccess = { result -> handleValidationResult(result) },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to validate guess"
                        )
                    }
                }
            )
        }
    }

    private fun handleValidationResult(result: ValidationResult) {
        _uiState.update { currentState ->
            if (result.isCorrect) {
                val solvedGroup = currentState.puzzle?.groups?.find { group ->
                    result.category?.let { group.theme == it } ?: false
                }
                val newSolvedGroups = currentState.solvedGroups + listOfNotNull(solvedGroup)
                val isGameWon = newSolvedGroups.size == 4
                val newPuzzlesSolved =
                    if (isGameWon) currentState.puzzlesSolved + 1 else currentState.puzzlesSolved
                if (isGameWon) {
                    savedStateHandle["puzzlesSolved"] = newPuzzlesSolved
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
                val availableWords = puzzle.words.filterNot { it in solvedWords }.shuffled()
                val updatedWords = (availableWords + solvedWords).distinct()
                currentState.copy(puzzle = puzzle.copy(words = updatedWords))
            } ?: currentState
        }
    }

    fun restartGame() {
        _uiState.update {
            it.copy(
                puzzle = null,
                selectedWords = emptySet(),
                solvedGroups = emptyList(),
                gameStatus = GameStatus.PLAYING,
                remainingAttempts = 4,
                puzzleSource = PuzzleSource.NETWORK
            )
        }
        loadPuzzle()
    }

    fun newGame(seed: Int? = null) {
        _uiState.update {
            it.copy(
                puzzle = null,
                selectedWords = emptySet(),
                solvedGroups = emptyList(),
                gameStatus = GameStatus.PLAYING,
                remainingAttempts = 4,
                puzzleSource = PuzzleSource.NETWORK
            )
        }
        loadPuzzle(seed = seed)
    }
}