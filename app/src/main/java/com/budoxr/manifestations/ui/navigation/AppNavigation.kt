package com.budoxr.manifestations.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.budoxr.Exercises.ui.JournalScreen
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
    navigateToJournals: onIntType
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
            navArgument("manifestation") { type = NavType.IntType },
        )) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page")
            val manifestationId = backStackEntry.arguments?.getInt("manifestation")

            ManifestationScreen(
                navController = navController,
                page = page ?: 0,
                manifestationId = manifestationId ?: 0,
                isDarkTheme = isDarkTheme,
                innerPadding = innerPadding,
                onEditMode = onEditMode,
                navigateToJournals = navigateToJournals
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
                lessonDay =  lessonDay ?: 0,
                innerPadding = innerPadding,
                onEditMode = onEditMode,
            )
        }
        
    }
}

private const val TAG = "che.AppNavigation"