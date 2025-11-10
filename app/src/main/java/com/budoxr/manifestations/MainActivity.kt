package com.budoxr.manifestations

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.data.repositories.LocalPref.LOCAL_PREF
import com.budoxr.manifestations.di.Modules.appModule
import com.budoxr.manifestations.di.Modules.databaseModule
import com.budoxr.manifestations.di.Modules.moshiModule
import com.budoxr.manifestations.di.Modules.workerModule
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.ui.features.MainScreen
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : ComponentActivity() {

    private lateinit var requestedPermission: String

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.tag(TAG).d("registerForActivityResult() -> returned, granted: $granted")
        if (granted) {
            // permission is granted
        } else {
            Timber.tag(TAG).d("permission: \'$requestedPermission\', denied.")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
//                val value = intent.extras!![key]
                val value = intent.extras?.getString(key)  // fix the deprecated warning of the line above
                Timber.tag(TAG).d("Key: $key Value: $value")
            }
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Timber.tag("MyApp").d("Debug mode enabled")
        } else {
            Timber.tag("MyApp").d("Release mode disabled")
        }

        try {
            startKoin {
                androidContext(this@MainActivity)
                androidLogger()
                modules(appModule, moshiModule, databaseModule, workerModule)
            }
        } catch (ex: KoinApplicationAlreadyStartedException) {
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
                    MainScreen(
                        askReadExternalStoragePermission = this::askReadExternalStoragePermission,
                        askWriteExternalStoragePermission = this::askWriteExternalStoragePermission,
                    )
                }
            }

        }
    }


    fun askReadExternalStoragePermission(): Boolean {
        Timber.tag(TAG).i("askReadExternalStoragePermission() -> called")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            Timber.tag(TAG).i("Read External Storage permission granted")
            return true

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // TODO: display an educational UI explaining to the user the features that will be enabled
            //       by them granting the READ_EXTERNAL_STORAGE permission. This UI should provide the user
            //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
            //       If the user selects "No thanks," allow the user to continue without notifications.
        } else {
            // Directly ask for the permission
            requestedPermission = Manifest.permission.READ_EXTERNAL_STORAGE
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return false
    }

    fun askWriteExternalStoragePermission(): Boolean {
        Timber.tag(TAG).i("askWriteExternalStoragePermission() -> called")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            Timber.tag(TAG).i("Write External Storage permission granted")
            return true

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // TODO: display an educational UI explaining to the user the features that will be enabled
            //       by them granting the WRITE_EXTERNAL_STORAGE permission. This UI should provide the user
            //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
            //       If the user selects "No thanks," allow the user to continue without notifications.
        } else {
            // Directly ask for the permission
            requestedPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return false
    }

}

private const val TAG = "che.MainActivity"