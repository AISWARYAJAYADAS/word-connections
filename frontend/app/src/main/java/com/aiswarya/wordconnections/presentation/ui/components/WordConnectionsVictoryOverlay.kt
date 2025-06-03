package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiswarya.wordconnections.R
import kotlinx.coroutines.delay

// Professional font family
val Poppins = FontFamily(
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
)

@Composable
fun WordConnectionsVictoryOverlay(
    modifier: Modifier = Modifier,
    puzzlesSolved: Int,
    onPlayAgain: () -> Unit,
    onShare: () -> Unit,
    visible: Boolean = true,
    enableHaptics: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    // Professional color system
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Success colors
    val successGreen = Color(0xFF10B981)
    val accentGold = Color(0xFFF59E0B)
    val gradientStart = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val gradientEnd = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF)

    // Refined confetti colors
    val confettiColors = listOf(
        successGreen,
        accentGold,
        primaryColor,
        MaterialTheme.colorScheme.tertiary
    )

    // Smooth rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "trophy_rotation"
    )

    // Gentle pulse for emphasis
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Staggered animation states
    var showIcon by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            showIcon = true
            delay(200)
            showContent = true
            delay(300)
            showButtons = true
        } else {
            showIcon = false
            showContent = false
            showButtons = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = if (isDark) 0.8f else 0.7f)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background blur effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(2.dp)
        )

        // Confetti layer
        WordConnectionsConfetti(
            modifier = Modifier.fillMaxSize(),
            colors = confettiColors
        )

        // Main victory dialog
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(400)),
            exit = scaleOut(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                shadowElevation = 24.dp,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    gradientStart.copy(alpha = 0.05f),
                                    gradientEnd.copy(alpha = 0.02f)
                                )
                            )
                        )
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Success icon with professional styling
                    AnimatedVisibility(
                        visible = showIcon,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(300))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            // Subtle background circle
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                successGreen.copy(alpha = 0.1f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .graphicsLayer {
                                        scaleX = pulse
                                        scaleY = pulse
                                    }
                            )

                            // Main icon container
                            Surface(
                                modifier = Modifier
                                    .size(80.dp)
                                    .graphicsLayer {
                                        rotationZ = rotation * 0.1f // Subtle rotation
                                    },
                                shape = CircleShape,
                                color = successGreen.copy(alpha = 0.15f),
                                shadowElevation = 8.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Success",
                                        tint = successGreen,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .semantics {
                                                contentDescription = "Victory success icon"
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Title and message section
                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Main title
                            Text(
                                text = "Congratulations!",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = onSurfaceColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.semantics {
                                    contentDescription = "Victory congratulations title"
                                }
                            )

                            // Subtitle
                            Text(
                                text = "Puzzle Completed Successfully",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                ),
                                color = successGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.semantics {
                                    contentDescription = "Puzzle completion subtitle"
                                }
                            )

                            // Description
                            Text(
                                text = "You've successfully solved today's word connections puzzle! Your pattern recognition skills are impressive.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 24.sp
                                ),
                                color = onSurfaceColor.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .semantics {
                                        contentDescription = "Victory description message"
                                    }
                            )
                        }
                    }

                    // Statistics section
                    AnimatedVisibility(
                        visible = showContent,
                        enter = scaleIn(
                            animationSpec = tween(300, delayMillis = 100)
                        ) + fadeIn(animationSpec = tween(300, delayMillis = 100))
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${maxOf(1, puzzlesSolved)}",
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 36.sp
                                        ),
                                        color = primaryColor
                                    )
                                    Text(
                                        text = if (puzzlesSolved == 1) "Puzzle Solved Today" else "Puzzles Solved Today",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = onSurfaceColor.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Action buttons
                    AnimatedVisibility(
                        visible = showButtons,
                        enter = slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400, delayMillis = 150)
                        ) + fadeIn(animationSpec = tween(400, delayMillis = 150))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Primary action - Play Again
                            Button(
                                onClick = {
                                    if (enableHaptics) haptic.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onPlayAgain()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 6.dp,
                                    pressedElevation = 2.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Play New Puzzle",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                )
                            }

                            // Secondary action - Share
                            OutlinedButton(
                                onClick = {
                                    if (enableHaptics) haptic.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onShare()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    primaryColor
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = primaryColor
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Share Achievement",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}