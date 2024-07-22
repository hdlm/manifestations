package com.budoxr.manifestations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.budoxr.manifestations.ui.MainScreen
import com.budoxr.manifestations.ui.theme.ManifestationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManifestationsTheme {
                MainScreen()
            }
        }
    }
}
