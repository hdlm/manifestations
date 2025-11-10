package com.budoxr.manifestations.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens (
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    val baseRoute: String
        get() = route.substringBefore('/')

    object Expand: Screens(route = "Expand", title = "Expand", Icons.Default.Menu)
    object LessonScreen: Screens(route = "LessonScreen", title = "Lessons", Icons.Default.School)
    object ManifestationScreen: Screens(route = "ManifestationScreen", title = "Manifestations", Icons.Default.EventAvailable)
    object ManifestationAddFormScreen: Screens(route = "ManifestationAddFormScreen", title = "Manifestations", Icons.Default.EventAvailable)
    object JournalScreen: Screens(route = "JournalScreen/{page}/{manifestation}/{lesson}", title = "Journals", Icons.AutoMirrored.Filled.EventNote)
    object SettingScreen: Screens(route = "SettingScreen", title = "Setting", Icons.Default.Settings)

    companion object {
        // When I iterate 'LessonScreen' it returns null, this is because
        // A runtime initialization failure where a Kotlin singleton object (Screens.LessonScreen)
        // becomes null within a static list (Screens.allScreens), which is anomalous behavior.
//        val allScreens = listOf(
//            Expand,
//            LessonScreen,
//            ManifestationScreen,
//            JournalScreen,
//            SettingScreen
//        )
    }
}