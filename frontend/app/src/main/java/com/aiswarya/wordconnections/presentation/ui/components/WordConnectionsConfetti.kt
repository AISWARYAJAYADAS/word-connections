package com.aiswarya.wordconnections.presentation.ui.components


import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun WordConnectionsConfetti(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF1493), // Deep Pink
        Color(0xFF00BFFF), // Deep Sky Blue
        Color(0xFF7CFC00)  // Lawn Green
    )
) {
    val confettiCount = 50
    val confetti = remember { List(confettiCount) { ConfettiPiece(colors.random()) } }
    Box(modifier = modifier.fillMaxSize()) {
        confetti.forEachIndexed { index, piece ->
            key(index) {
                val infiniteTransition = rememberInfiniteTransition()
                val x by infiniteTransition.animateFloat(
                    initialValue = piece.startX,
                    targetValue = piece.endX,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = piece.duration, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                val y by infiniteTransition.animateFloat(
                    initialValue = piece.startY,
                    targetValue = piece.endY,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = piece.duration, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = piece.duration, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                Box(
                    modifier = Modifier
                        .offset(x = x.dp, y = y.dp)
                        .rotate(rotation)
                        .size(piece.size.dp)
                        .background(piece.color, shape = RectangleShape)
                )
            }
        }
    }
}

private data class ConfettiPiece(
    val color: Color,
    val startX: Float = Random.nextFloat() * 400 - 200,
    val startY: Float = Random.nextFloat() * -100 - 50,
    val endX: Float = startX + Random.nextFloat() * 100 - 50,
    val endY: Float = Random.nextFloat() * 1000 + 500,
    val size: Float = Random.nextFloat() * 8 + 4,
    val duration: Int = Random.nextInt(3000) + 2000,
    val delay: Int = Random.nextInt(1000)
)