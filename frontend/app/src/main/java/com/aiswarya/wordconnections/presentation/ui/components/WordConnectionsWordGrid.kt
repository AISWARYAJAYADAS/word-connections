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
import androidx.compose.ui.unit.dp
import com.aiswarya.wordconnections.domain.model.Difficulty
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
    val cardSize = (screenWidth - 24.dp) / 4 // Slightly larger cards for better text fit

    val solvedWords = remember(solvedGroups) { solvedGroups.flatMap { it.words }.toSet() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(if (isLandscape) 8.dp else 6.dp)
    ) {
        items(words, key = { it }) { word ->
            val isSelected = word in selectedWords
            val isSolved = word in solvedWords
            val groupColor = solvedGroups.find { word in it.words }?.let {
                when (it.difficulty) {
                    Difficulty.YELLOW -> Color(0xFFFBC02D)
                    Difficulty.GREEN -> Color(0xFF4CAF50)
                    Difficulty.BLUE -> Color(0xFF0288D1)
                    Difficulty.PURPLE -> Color(0xFF7B1FA2)
                }
            }

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
                    modifier = Modifier.size(cardSize)
                )
            }
        }
    }
}