package com.budoxr.manifestations.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.budoxr.Exercises.ui.ExerciseScreen
import com.budoxr.manifestations.commons.onBooleanType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.ui.LessonScreen
import com.budoxr.manifestations.ui.ManifestationScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDest: Screens,
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
) {

    NavHost(navController = navController, startDestination = "${startDest.route.substringBefore('/')}/0") {
        
        composable(Screens.LessonScreen.route) {
            LessonScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                innerPadding = innerPadding,
            )
        }

        composable(Screens.ManifestationScreen.route, arguments = listOf(
            navArgument("page") { type = NavType.IntType },
        )) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page")
            ManifestationScreen(
                navController = navController,
                page = page ?: 0,
                isDarkTheme = isDarkTheme,
                innerPadding = innerPadding,
            )
        }

        composable(Screens.ExerciseScreen.route, arguments = listOf(
            navArgument("page") { type = NavType.IntType },
        )) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page")
            ExerciseScreen(
                navController = navController,
                page = page ?: 0,
                innerPadding = innerPadding,
            )
        }
        
    }
}