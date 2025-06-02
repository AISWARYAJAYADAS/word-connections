package com.aiswarya.wordconnections.presentation.ui.components

import android.content.res.Configuration
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
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp

    // Dynamic columns based on screen size
    val columns = when {
        configuration.screenWidthDp > 900 -> 6  // Tablets
        isLandscape -> 5  // Landscape phones
        else -> 4  // Default (Portrait)
    }

    // Calculate card size dynamically
    val cardSize = (screenWidth - (16.dp * (columns + 1))) / columns

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
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