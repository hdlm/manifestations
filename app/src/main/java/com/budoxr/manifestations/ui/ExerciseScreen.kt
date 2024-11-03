package com.budoxr.Exercises.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.budoxr.manifestations.ui.theme.ManifestationsTheme


@Composable
fun ExerciseScreen(
    navController: NavController,
    page: Int,
    innerPadding: PaddingValues,
//    viewModel: ExerciseScreen = getViewModel()

) {
    Log.i(TAG, "compose / recompose")
    Surface(modifier = Modifier.padding(innerPadding)) {
        Text("Excercises")
    }
}

@Composable
@Preview(showBackground = true)
fun ExerciseScreenPreview() {

    ManifestationsTheme {

        Surface (modifier = Modifier.fillMaxSize()) {
            //TODO add here the composable function
        }

    }

}

private const val TAG =  "che.ExerciseScreen"