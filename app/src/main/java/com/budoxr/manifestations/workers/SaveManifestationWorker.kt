package com.budoxr.manifestations.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class SaveManifestationWorker (context: Context, params: WorkerParameters)
    : KoinComponent, CoroutineWorker(context, params) {

    private val manifestationInsertUseCase : ManifestationInsertUseCase by inject()

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork() -> save the Manifestation to the database")

        val manifestationJson = inputData.getString("REGISTER") ?: return Result.failure()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(ManifestationModel::class.java)
        val manifestation = jsonAdapter.fromJson(manifestationJson)

        return try {
            manifestationInsertUseCase.invoke(manifestation!!.toEntity())
            return Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "doWork() -> error saving manifestation", ex)
            Result.failure()
        }

    }

}

private const val TAG = "che.SaveManifestationWorker"