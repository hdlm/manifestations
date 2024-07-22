package com.budoxr.manifestations.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.budoxr.Exercises.ui.ExerciseScreen
import com.budoxr.manifestations.ui.LessonScreen
import com.budoxr.manifestations.ui.ManifestationScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDest: Screens,
    innerPadding: PaddingValues
) {

    NavHost(navController = navController, startDestination = startDest.route ) {
        
        composable(Screens.LessonScreen.route) {
            LessonScreen(
                navController = navController,
                innerPadding = innerPadding
            )
        }

        composable(Screens.ManifestationScreen.route) {
            ManifestationScreen(
                navController = navController,
                innerPadding = innerPadding
            )
        }

        composable(Screens.ExerciseScreen.route) {
            ExerciseScreen(
                navController = navController,
                innerPadding = innerPadding
            )
        }
        
    }
}