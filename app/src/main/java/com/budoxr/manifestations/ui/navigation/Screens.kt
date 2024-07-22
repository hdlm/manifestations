package com.budoxr.manifestations.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens (
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Expand: Screens(route = "Expand", title = "Expand", Icons.Default.Menu)
    object LessonScreen: Screens(route = "LessonScreen", title = "Lessons", Icons.Default.School)
    object ManifestationScreen: Screens(route = "ManifestScreen", title = "Manifestations", Icons.Default.EventAvailable)
    object ExerciseScreen: Screens(route = "ExerciseScreen", title = "Exercises", Icons.AutoMirrored.Filled.EventNote)
}