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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    LaunchedEffect(isPressed, isSelected) {
        scale = when {
            isPressed -> 0.95f
            isSelected -> 1.03f
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
        animationSpec = tween(durationMillis = 200),
        label = "background_color"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSolved -> Color.White
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "text_color"
    )

    Box(
        modifier = modifier
            .padding(4.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .shadow(
                elevation = if (isSelected || isSolved) 6.dp else 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
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
                enabled = enabled,
                onClick = onClick
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        OptimizedWordText(
            text = word,
            fontWeight = if (isSolved) FontWeight.Bold else FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp) // Reduced padding
        )
    }
}

@Composable
private fun OptimizedWordText(
    text: String,
    fontWeight: FontWeight?,
    color: Color,
    modifier: Modifier = Modifier
) {
    val displayText = remember(text) {
        when {
            text.length <= 10 -> text.uppercase()
            " -".any { it in text } -> text
                .replace(" -", "-\n")
                .replace(" ", "\n")
                .uppercase()
            else -> text.uppercase()
        }
    }

    Text(
        text = displayText,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 12.sp,  // Reduced from 14.sp
            lineHeight = 14.sp, // Tight line height (was 18.sp)
            letterSpacing = 0.1.sp, // Reduced from 0.15.sp
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
        fontWeight = fontWeight,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWordConnectionsWordCard() {
    WordConnectionsTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WordConnectionsWordCard(
                word = "SUN",
                isSelected = false,
                onClick = {}
            )
            WordConnectionsWordCard(
                word = "MOTHER-IN-LAW",
                isSelected = true,
                onClick = {}
            )
            WordConnectionsWordCard(
                word = "ICE CREAM",
                isSolved = false,
                onClick = {},
                isSelected = true
            )
            WordConnectionsWordCard(
                word = "EXTRAORDINARY",
                isSolved = true,
                groupColor = Color(0xFFFBC02D),
                onClick = {},
                isSelected = true
            )
        }
    }
}