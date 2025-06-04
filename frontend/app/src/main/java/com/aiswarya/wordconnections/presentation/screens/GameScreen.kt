package com.aiswarya.wordconnections.presentation.screens

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.aiswarya.wordconnections.presentation.ui.components.*
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
            LandscapeTabletLayout(uiState, viewModel, paddingValues)
        } else {
            PortraitMobileLayout(uiState, viewModel, paddingValues)
        }

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
                    val shareText = "I solved ${uiState.puzzlesSolved} puzzles in WordConnect today! 🎉 #WordConnect"
                    val intent = Intent(Intent.ACTION_SEND).apply {
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
                modifier = Modifier.fillMaxWidth(),
                selectedCount = uiState.selectedWords.size,
                onSubmit = viewModel::submitGuess,
                onShuffle = viewModel::shuffleWords,
                onClear = viewModel::clearSelection,
                onNewGame = { viewModel.newGame() },
                onRestart = { viewModel.restartGame() },
                enabled = uiState.gameStatus == GameStatus.PLAYING,
                showGameControls = true
            )
        }
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
            modifier = Modifier.fillMaxWidth(),
            selectedCount = uiState.selectedWords.size,
            onSubmit = viewModel::submitGuess,
            onShuffle = viewModel::shuffleWords,
            onClear = viewModel::clearSelection,
            onNewGame = { viewModel.newGame() },
            onRestart = { viewModel.restartGame() },
            enabled = uiState.gameStatus == GameStatus.PLAYING,
            showGameControls = true
        )
    }
}