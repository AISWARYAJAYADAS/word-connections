package com.aiswarya.wordconnections.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.aiswarya.wordconnections.domain.model.Difficulty
import com.aiswarya.wordconnections.domain.model.PuzzleGroup

@Composable
fun WordConnectionsWordGrid(
    modifier: Modifier = Modifier,
    words: List<String>,
    selectedWords: Set<String>,
    solvedGroups: List<PuzzleGroup>,
    onWordToggle: (String) -> Unit,
    enabled: Boolean = true,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp

    // Calculate columns based on screen size and orientation
    val columns = when {
        configuration.screenWidthDp > 900 -> if (isLandscape) 6 else 5  // Tablets
        isLandscape -> 5  // Landscape phones
        else -> 4  // Portrait phones
    }

    // Calculate card size with minimum size constraints
    val minCardSize = if (isLandscape) 80.dp else 70.dp
    val calculatedSize = (screenWidth - (16.dp * (columns + 1))) / columns
    val cardSize = maxOf(calculatedSize, minCardSize)

    // Add key to force recomposition on orientation change
    val gridKey = remember(configuration.orientation) { configuration.orientation }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth(),
               // .key(gridKey),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(words, key = { it }) { word ->
                val isSolved = solvedGroups.any { group -> word in group.words }
                val groupColor = solvedGroups.find { word in it.words }?.let { group ->
                    when (group.difficulty) {
                        Difficulty.YELLOW -> Color(0xFFFBC02D)
                        Difficulty.GREEN -> Color(0xFF4CAF50)
                        Difficulty.BLUE -> Color(0xFF0288D1)
                        Difficulty.PURPLE -> Color(0xFF7B1FA2)
                    }
                }

                WordConnectionsWordCard(
                    word = word,
                    isSelected = selectedWords.contains(word),
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