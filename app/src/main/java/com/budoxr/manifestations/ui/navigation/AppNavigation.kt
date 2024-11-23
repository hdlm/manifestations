package com.budoxr.manifestations.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.budoxr.Exercises.ui.ExerciseScreen
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.ui.LessonScreen
import com.budoxr.manifestations.ui.ManifestationScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDest: Screens,
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    onEditMode: onIntType,
) {

    NavHost(navController = navController, startDestination = "${startDest.route}") {
//    NavHost(navController = navController, startDestination = "${startDest.route.substringBefore('/')}/0") {

        composable(Screens.LessonScreen.route) {
            LessonScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                innerPadding = innerPadding,
            )
        }

        composable(Screens.ManifestationScreen.route, arguments = listOf(
            navArgument("page") { type = NavType.IntType },
            navArgument("id") { type = NavType.IntType },
        )) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page")
            val id = backStackEntry.arguments?.getInt("id")

            ManifestationScreen(
                navController = navController,
                page = page ?: 0,
                id = id ?: 0,
                isDarkTheme = isDarkTheme,
                innerPadding = innerPadding,
                onEditMode = onEditMode,
            )
        }

        composable(Screens.JournalScreen.route, arguments = listOf(
            navArgument("page") { type = NavType.IntType },
            navArgument("id") { type = NavType.LongType },
        )) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page")
            val id = backStackEntry.arguments?.getLong("id")
            ExerciseScreen(
                navController = navController,
                page = page ?: 0,
                id = id ?: 0,
                innerPadding = innerPadding,
            )
        }
        
    }
}