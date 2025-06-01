package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiswarya.wordconnections.presentation.ui.theme.WordConnectionsTheme

@Composable
fun WordConnectionsWordCard(
    word: String,
    isSelected: Boolean,
    isSolved: Boolean = false,
    groupColor: Color? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var scale by remember { mutableFloatStateOf(1f) }
    val haptic = LocalHapticFeedback.current

    // Pulse animation for selection
    LaunchedEffect(isPressed, isSelected) {
        scale = when {
            isPressed -> 0.92f
            isSelected -> 1.05f // Slight pulse for selected state
            else -> 1f
        }
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSolved && groupColor != null -> groupColor
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(durationMillis = 300),
        label = "background_color"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSolved -> Color.White.copy(alpha = 0.95f)
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "text_color"
    )

    // Dynamic font size with higher minimum
    val fontSize = with(LocalDensity.current) {
        when (word.length) {
            in 0..6 -> 22.sp
            in 7..12 -> 20.sp
            in 13..18 -> 18.sp
            else -> 16.sp // Increased minimum size
        }
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .shadow(elevation = if (isSelected || isSolved) 8.dp else 4.dp, shape = RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.8f)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                enabled = enabled
            ) { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word.uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize,
                letterSpacing = 0.5.sp
            ),
            fontWeight = if (isSolved) FontWeight.Bold else FontWeight.SemiBold,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp), // Increased padding
           // contentDescription = "Word: $word"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWordCard() {
    WordConnectionsTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            WordConnectionsWordCard(
                word = "To",
                isSelected = false,
                onClick = {}
            )
            WordConnectionsWordCard(
                word = "Computer",
                isSelected = true,
                onClick = {}
            )
            WordConnectionsWordCard(
                word = "Supercalifragilistic",
                isSelected = true,
                isSolved = true,
                groupColor = Color(0xFF4CAF50),
                onClick = {}
            )
        }
    }
}