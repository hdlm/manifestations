package com.budoxr.manifestations.presentation.usecase

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent

class CheckPermissionUseCase : KoinComponent {

    operator fun invoke(grantType: String, context: Context): Flow<Boolean> = flow {
        if (ContextCompat.checkSelfPermission( context, grantType ) == PackageManager.PERMISSION_DENIED
        ) {
            Log.d(TAG, "permission denied to $grantType, requesting it" )
            emit(false)
        } else {
            Log.d(TAG, "permission granted to $grantType" )
            emit(true)
        }
    }

}

private const val TAG = "che.CheckPermissionUseCase"