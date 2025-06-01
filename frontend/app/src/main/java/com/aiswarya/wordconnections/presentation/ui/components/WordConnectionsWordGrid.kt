package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aiswarya.wordconnections.domain.model.PuzzleGroup

@Composable
fun WordConnectionsWordGrid(
    words: List<String>,
    selectedWords: Set<String>,
    solvedGroups: List<PuzzleGroup>,
    onWordToggle: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp
    val minCellWidth = (screenWidth / 4.5f).coerceAtLeast(80.dp)
    val maxCellWidth = (screenWidth / 3.5f).coerceAtMost(if (isLandscape) 180.dp else 200.dp)

    val solvedWords = remember(solvedGroups) { solvedGroups.flatMap { it.words }.toSet() }
    val density = LocalDensity.current


    // Calculate optimal card size (4x4 grid with spacing)
    val cardSize = remember(screenWidth, isLandscape) {
        (screenWidth - 32.dp) / 4 // 32dp accounts for padding (16dp) and spacing (8dp * 3)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Fixed 4x4 grid like NYT Connections
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(if (isLandscape) 16.dp else 12.dp)
    ) {
        items(words, key = { it }) { word ->
            val isSelected = word in selectedWords
            val isSolved = word in solvedWords
            val groupColor = solvedGroups.find { word in it.words }?.let {
                when (it.difficulty) {
                    com.aiswarya.wordconnections.domain.model.Difficulty.YELLOW -> Color(0xFFFBC02D)
                    com.aiswarya.wordconnections.domain.model.Difficulty.GREEN -> Color(0xFF4CAF50)
                    com.aiswarya.wordconnections.domain.model.Difficulty.BLUE -> Color(0xFF0288D1)
                    com.aiswarya.wordconnections.domain.model.Difficulty.PURPLE -> Color(0xFF7B1FA2)
                }
            }

            // Estimate width based on word length and font size
            val fontSize = when (word.length) {
                in 0..6 -> 22f
                in 7..12 -> 20f
                in 13..18 -> 18f
                else -> 14f // Reduced for very long words
            }
            val textWidth = with(density) { (word.length * fontSize * 0.8f).toDp() } // Increased factor for uppercase
            val cardWidth = (textWidth + 32.dp).coerceIn(minCellWidth, maxCellWidth) // Add padding buffer

            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)
                ) + fadeIn(),
                exit = scaleOut() + fadeOut(),
                label = "WordCardAnimation"
            ) {
                WordConnectionsWordCard(
                    word = word,
                    isSelected = isSelected,
                    isSolved = isSolved,
                    groupColor = groupColor,
                    onClick = { onWordToggle(word) },
                    enabled = enabled && !isSolved,
                    modifier = Modifier.size(cardSize) // Uniform square size
//                    modifier = Modifier
//                        .width(cardWidth)
//                        .sizeIn(minHeight = 80.dp)
                )
            }
        }
    }
}