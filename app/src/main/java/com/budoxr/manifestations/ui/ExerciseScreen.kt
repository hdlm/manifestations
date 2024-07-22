package com.budoxr.Exercises.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun ExerciseScreen(
    navController: NavController,
    innerPadding: PaddingValues,
//    viewModel: ExerciseScreen = getViewModel()

) {
    Log.i(TAG, "compose / recompose")
    Surface(modifier = Modifier.padding(innerPadding)) {
        Text("Excercises")
    }
}

private const val TAG =  "ExerciseScreen"