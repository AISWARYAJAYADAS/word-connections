package com.aiswarya.wordconnections.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aiswarya.wordconnections.presentation.viewmodel.GameStatus

@Composable
fun WordConnectionsGameControls(
    selectedCount: Int,
    onSubmit: () -> Unit,
    onShuffle: () -> Unit,
    onClear: () -> Unit,
    onNewGame: () -> Unit,
    onRestart: () -> Unit,
    enabled: Boolean = true,
    showGameControls: Boolean = true,
    gameStatus: GameStatus,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.screenWidthDp > 600

    // Dynamic sizing based on screen
    val buttonHeight = when {
        isTablet -> 48.dp  // Tablets keep normal size
        isLandscape -> 40.dp  // Landscape phones: compact
        else -> 44.dp  // Portrait phones: slightly reduced
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showGameControls) {
                // Submit & Clear Row (Compact)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSubmit,
                        enabled = enabled && selectedCount == 4,
                        modifier = Modifier
                            .weight(1f)
                            .height(buttonHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "SUBMIT (${selectedCount}/4)",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = onClear,
                        enabled = enabled && selectedCount > 0,
                        modifier = Modifier
                            .height(buttonHeight)
                            .weight(0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CLEAR", style = MaterialTheme.typography.labelLarge)
                    }
                }

                // Shuffle & Restart Row (Even more compact)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onShuffle,
                        enabled = enabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(buttonHeight - 4.dp),  // Slightly smaller
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SHUFFLE", style = MaterialTheme.typography.labelMedium)
                    }

                    FilledTonalButton(
                        onClick = onRestart,
                        enabled = enabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(buttonHeight - 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("RESTART", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // New Game Button (Full width but compact)
            OutlinedButton(
                onClick = onNewGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight - 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("NEW GAME", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}