package com.aiswarya.wordconnections.presentation.screens

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.aiswarya.wordconnections.presentation.viewmodel.GameUiState
import com.aiswarya.wordconnections.presentation.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val context = LocalContext.current

    // Adaptive padding
    val paddingValues = when {
        isTablet -> PaddingValues(24.dp)
        isLandscape -> PaddingValues(16.dp)
        else -> PaddingValues(12.dp)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        if (isLandscape || isTablet) {
            LandscapeTabletLayout(
                uiState = uiState,
                viewModel = viewModel,
                paddingValues = paddingValues
            )
        } else {
            PortraitMobileLayout(
                uiState = uiState,
                viewModel = viewModel,
                paddingValues = paddingValues
            )
        }

        // Overlays
        if (uiState.isLoading) {
            WordConnectionsLoadingState(
                message = "Loading puzzle...",
                modifier = Modifier.fillMaxSize()
            )
        }

        uiState.errorMessage?.let { error ->
            WordConnectionsErrorMessage(
                message = error,
                onDismiss = { viewModel.clearError() },
                onRetry = { viewModel.loadPuzzle() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (uiState.gameStatus == GameStatus.WON) {
            WordConnectionsVictoryOverlay(
                puzzlesSolved = uiState.puzzlesSolved,
                onPlayAgain = { viewModel.newGame() },
                onShare = {
                    val shareText = "I solved ${uiState.puzzlesSolved} puzzles in Word Connections today! 🎉 #WordConnections"
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, "Share your victory!"))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (uiState.gameStatus == GameStatus.LOST) {
            WordConnectionsCompletionState(
                message = "Game Over! Try again?",
                onRetry = { viewModel.restartGame() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LandscapeTabletLayout(
    uiState: GameUiState,
    viewModel: GameViewModel,
    paddingValues: PaddingValues
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game Board (70% width)
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WordConnectionsGameHeader(
                remainingAttempts = uiState.remainingAttempts,
                solvedGroups = uiState.solvedGroups.size,
                totalGroups = 4,
                gameStatus = uiState.gameStatus,
                modifier = Modifier.fillMaxWidth()
            )

            WordConnectionsWordGrid(
                words = uiState.puzzle?.words ?: emptyList(),
                selectedWords = uiState.selectedWords,
                solvedGroups = uiState.solvedGroups,
                onWordToggle = viewModel::toggleWordSelection,
                enabled = uiState.gameStatus == GameStatus.PLAYING,
                modifier = Modifier.weight(1f)
            )

            WordConnectionsGameControls(
                selectedCount = uiState.selectedWords.size,
                onSubmit = viewModel::submitGuess,
                onShuffle = viewModel::shuffleWords,
                onClear = viewModel::clearSelection,
                onNewGame = { viewModel.newGame() },
                onRestart = { viewModel.restartGame() },
                enabled = uiState.gameStatus == GameStatus.PLAYING,
                showGameControls = true,
                gameStatus = uiState.gameStatus,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Solved Groups (30% width)
        WordConnectionsSolvedGroups(
            groups = uiState.solvedGroups,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(start = 16.dp)
        )
    }
}

@Composable
private fun PortraitMobileLayout(
    uiState: GameUiState,
    viewModel: GameViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WordConnectionsGameHeader(
            remainingAttempts = uiState.remainingAttempts,
            solvedGroups = uiState.solvedGroups.size,
            totalGroups = 4,
            gameStatus = uiState.gameStatus,
            modifier = Modifier.fillMaxWidth()
        )

        WordConnectionsSolvedGroups(
            groups = uiState.solvedGroups,
            modifier = Modifier.fillMaxWidth()
        )

        WordConnectionsWordGrid(
            words = uiState.puzzle?.words ?: emptyList(),
            selectedWords = uiState.selectedWords,
            solvedGroups = uiState.solvedGroups,
            onWordToggle = viewModel::toggleWordSelection,
            enabled = uiState.gameStatus == GameStatus.PLAYING,
            modifier = Modifier.weight(1f)
        )

        WordConnectionsGameControls(
            selectedCount = uiState.selectedWords.size,
            onSubmit = viewModel::submitGuess,
            onShuffle = viewModel::shuffleWords,
            onClear = viewModel::clearSelection,
            onNewGame = { viewModel.newGame() },
            onRestart = { viewModel.restartGame() },
            enabled = uiState.gameStatus == GameStatus.PLAYING,
            showGameControls = true,
            gameStatus = uiState.gameStatus,
            modifier = Modifier.fillMaxWidth()
        )
    }
}