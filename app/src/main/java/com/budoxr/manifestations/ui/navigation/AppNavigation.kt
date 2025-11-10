package com.budoxr.manifestations.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.budoxr.Exercises.ui.JournalScreen
import com.budoxr.manifestations.ui.features.lessons.LessonScreen
import com.budoxr.manifestations.ui.features.manifestations.ManifestationAddFormScreen
import com.budoxr.manifestations.ui.features.manifestations.ManifestationScreen


@Composable
fun AppNavigation(
    navController: NavHostController,
    startDest: Screens,
    isDarkTheme: Boolean,
) {

    NavHost(navController = navController, startDestination = startDest.route) {
//    NavHost(navController = navController, startDestination = "${startDest.route.substringBefore('/')}/0") {

        composable(Screens.LessonScreen.route) { _ ->
            LessonScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
            )
        }

        composable(Screens.ManifestationScreen.route) { _ ->
            ManifestationScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
            )
        }

        composable(Screens.ManifestationAddFormScreen.route) { _ ->
            ManifestationAddFormScreen(
                isDarkTheme = isDarkTheme,
            )
        }


        composable(Screens.JournalScreen.route, arguments = listOf(
            navArgument("page") { type = NavType.IntType },
            navArgument("manifestation") { type = NavType.IntType },
            navArgument("lesson") { type = NavType.IntType },
        )) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page")
            val manifestationId = backStackEntry.arguments?.getInt("manifestation")
            val lessonDay = backStackEntry.arguments?.getInt("lesson")
            JournalScreen(
                navController = navController,
                page = page ?: 0,
                manifestationId = manifestationId ?: 0,
                lessonDay = lessonDay ?: 0,
                onEditMode = { _->},
            )
        }
        
    }
}

private const val TAG = "che.AppNavigation"