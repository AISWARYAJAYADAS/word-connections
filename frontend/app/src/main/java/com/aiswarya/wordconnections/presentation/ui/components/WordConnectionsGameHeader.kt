package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiswarya.wordconnections.presentation.viewmodel.GameStatus

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WordConnectionsGameHeader(
    remainingAttempts: Int,
    solvedGroups: Int,
    totalGroups: Int,
    gameStatus: GameStatus,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val padding = if (configuration.screenWidthDp > 600) 24.dp else 16.dp

    val animatedProgressColor by animateColorAsState(
        targetValue = when (gameStatus) {
            GameStatus.WON -> MaterialTheme.colorScheme.tertiaryContainer
            GameStatus.LOST -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(300)
    )

    val animatedAttemptsColor by animateColorAsState(
        targetValue = when (gameStatus) {
            GameStatus.WON -> MaterialTheme.colorScheme.tertiary
            GameStatus.LOST -> MaterialTheme.colorScheme.error
            else -> if (remainingAttempts > 0) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = padding, vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(totalGroups) { index ->
                    val isCompleted = index < solvedGroups
                    val isCurrent = index == solvedGroups && gameStatus == GameStatus.PLAYING

                    val segmentColor by animateColorAsState(
                        targetValue = if (isCompleted) animatedProgressColor
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        animationSpec = tween(300)
                    )

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(segmentColor)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                }
            }

            Surface(
                color = animatedAttemptsColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.widthIn(min = 72.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (gameStatus) {
                            GameStatus.WON -> Icons.Filled.Stars
                            GameStatus.LOST -> Icons.Filled.Warning
                            else -> Icons.Outlined.Favorite
                        },
                        contentDescription = when (gameStatus) {
                            GameStatus.WON -> "Puzzle completed"
                            GameStatus.LOST -> "Out of attempts"
                            else -> "Remaining attempts: $remainingAttempts"
                        },
                        tint = animatedAttemptsColor,
                        modifier = Modifier.size(20.dp)
                    )

                    AnimatedContent(
                        targetState = when (gameStatus) {
                            GameStatus.WON -> "WIN"
                            GameStatus.LOST -> "LOSE"
                            else -> "$remainingAttempts"
                        },
                        transitionSpec = {
                            slideInVertically { height -> height } + fadeIn() with
                                    slideOutVertically { height -> -height } + fadeOut()
                        }
                    ) { targetCount ->
                        Text(
                            text = targetCount,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = animatedAttemptsColor
                        )
                    }
                }
            }
        }
    }
}

