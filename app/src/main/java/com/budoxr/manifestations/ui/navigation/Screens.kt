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
    object Expand: Screens(route = "Expand", title = "Expand", Icons.Default.Menu)
    object LessonScreen: Screens(route = "LessonScreen", title = "Lessons", Icons.Default.School)
    object ManifestationScreen: Screens(route = "ManifestScreen/{page}/{id}", title = "Manifestations", Icons.Default.EventAvailable)
    object JournalScreen: Screens(route = "JournalScreen/{page}/{id}", title = "Journal", Icons.AutoMirrored.Filled.EventNote)
    object SettingScreen: Screens(route = "SettingScreen", title = "Setting", Icons.Default.Settings)
}