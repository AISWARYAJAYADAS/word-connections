package com.aiswarya.wordconnections.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsCompletionState
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsErrorMessage
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsGameControls
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsGameHeader
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsLoadingState
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsSolvedGroups
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsVictoryOverlay
import com.aiswarya.wordconnections.presentation.ui.components.WordConnectionsWordGrid
import com.aiswarya.wordconnections.presentation.viewmodel.GameStatus
import com.aiswarya.wordconnections.presentation.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val puzzle = uiState.puzzle
    val selectedWords = uiState.selectedWords
    val solvedGroups = uiState.solvedGroups
    val remainingAttempts = uiState.remainingAttempts
    val gameStatus = uiState.gameStatus
    val isLoading = uiState.isLoading
    val errorMessage = uiState.errorMessage
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Shake animation for incorrect guess
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(remainingAttempts) {
        if (remainingAttempts < 4 && gameStatus == GameStatus.PLAYING) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    10f at 50
                    -10f at 100
                    8f at 150
                    -8f at 200
                    6f at 250
                    -6f at 300
                    0f at 350
                }
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header with proper spacing
            WordConnectionsGameHeader(
                remainingAttempts = remainingAttempts,
                solvedGroups = solvedGroups.size,
                totalGroups = 4,
                gameStatus = gameStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            // Main content area with proper weight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(x = shakeOffset.value.dp)
            ) {
                if (puzzle != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WordConnectionsSolvedGroups(
                            groups = solvedGroups,
                            modifier = Modifier.fillMaxWidth()
                        )

                        WordConnectionsWordGrid(
                            words = puzzle.words,
                            selectedWords = selectedWords,
                            solvedGroups = solvedGroups,
                            onWordToggle = { viewModel.toggleWordSelection(it) },
                            enabled = gameStatus == GameStatus.PLAYING,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }

            // Footer controls with proper spacing
            WordConnectionsGameControls(
                selectedCount = selectedWords.size,
                onSubmit = { viewModel.submitGuess() },
                onShuffle = { viewModel.shuffleWords() },
                onClear = { viewModel.clearSelection() },
                onNewGame = { viewModel.newGame() },
                onRestart = { viewModel.restartGame() },
                enabled = gameStatus == GameStatus.PLAYING,
                showGameControls = gameStatus == GameStatus.PLAYING,
                gameStatus = gameStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Overlays
        if (isLoading) {
            WordConnectionsLoadingState(
                message = "Loading puzzle...",
                modifier = Modifier.fillMaxSize()
            )
        }

        if (gameStatus == GameStatus.WON) {
            WordConnectionsVictoryOverlay(
                onPlayAgain = { viewModel.newGame() },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (gameStatus == GameStatus.LOST) {
            WordConnectionsCompletionState(
                message = "Game Over! Try again?",
                onRetry = { viewModel.restartGame() },
                modifier = Modifier.fillMaxSize()
            )
        }

        errorMessage?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                WordConnectionsErrorMessage(
                    message = it,
                    onDismiss = { viewModel.clearError() },
                    onRetry = { viewModel.loadPuzzle() }
                )
            }
        }
    }
}