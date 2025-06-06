package com.aiswarya.wordconnections.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aiswarya.wordconnections.data.local.preferences.UserPreferences
import com.aiswarya.wordconnections.presentation.screens.GameScreen
import com.aiswarya.wordconnections.presentation.screens.InstructionsScreen
import com.aiswarya.wordconnections.presentation.viewmodel.GameViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun WordConnectionsNavHost(
    userPreferences: UserPreferences,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val hasSeenInstructions by userPreferences.hasSeenInstructions.collectAsState(initial = false)
    val gameViewModel: GameViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = if (hasSeenInstructions) "game" else "instructions",
        modifier = modifier
    ) {
        composable("instructions") {
            InstructionsScreen(
                onStartPlaying = {
                    coroutineScope.launch {
                        userPreferences.updateHasSeenInstructions(true)
                        navController.navigate("game") {
                            popUpTo("instructions") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("game") {
            GameScreen(
                viewModel = gameViewModel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
    }
}