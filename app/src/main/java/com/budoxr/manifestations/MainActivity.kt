package com.budoxr.manifestations

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.data.repositories.LocalPref.LOCAL_PREF
import com.budoxr.manifestations.di.Modules.appModule
import com.budoxr.manifestations.di.Modules.databaseModule
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.ui.MainScreen
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.error.ApplicationAlreadyStartedException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
//                val value = intent.extras!![key]
                val value = intent.extras?.getString(key)  // fix the deprecated warning of the line above
                Log.d(TAG, "Key: $key Value: $value")
            }
        }

        try {
            startKoin {
                androidContext(this@MainActivity)
                androidLogger()
                modules(appModule, databaseModule)
            }
        } catch (ex: ApplicationAlreadyStartedException) {
            // ignore
        }

        val pref = applicationContext.getSharedPreferences(LOCAL_PREF, MODE_PRIVATE)
        LocalPref.pref = pref
        if (LocalPref.getSession() == null)
            LocalPref.saveSession(SessionModel())

        setContent {

            ManifestationsTheme(dynamicColor = false) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color =  MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }

        }
    }
}

private const val TAG = "che.MainActivity"