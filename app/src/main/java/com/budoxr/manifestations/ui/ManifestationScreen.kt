package com.budoxr.manifestations.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ManifestationScreen(
    navController: NavController,
    innerPadding: PaddingValues,
//    viewModel: ManifestationScreen = getViewModel()

) {
    Log.i(TAG, "compose / recompose")
    Surface(modifier = Modifier.padding(innerPadding)) {
        Text("Manifestation")
    }
}

private const val TAG =  "ManifestationScreen"