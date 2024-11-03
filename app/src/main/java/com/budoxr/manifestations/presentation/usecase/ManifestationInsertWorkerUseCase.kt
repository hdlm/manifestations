package com.budoxr.manifestations.presentation.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.budoxr.manifestations.commons.CommonValues.MANIFESTATION_TAG
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.workers.SaveManifestationWorker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class ManifestationInsertWorkerUseCase(context: Context) : KoinComponent {
    private val workManager : WorkManager = WorkManager.getInstance(context)

    private var oldManifestationModel: ManifestationModel? = null

    fun saveManifestationWorker(manifestation: ManifestationModel, context: Context) {

        // save only if the manifestation is different from the old one
        if (oldManifestationModel == null) {
            oldManifestationModel = manifestation
        } else if (oldManifestationModel == manifestation) return

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(ManifestationModel::class.java)
        val manifestationJson = jsonAdapter.toJson(manifestation)

        val inputWorkData = workDataOf(
            "REGISTER" to manifestationJson
        )

        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        val saveWorkRequest = OneTimeWorkRequest.Builder(SaveManifestationWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputWorkData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//            .setInitialDelay(35, TimeUnit.SECONDS)
            .addTag(tag = MANIFESTATION_TAG)
            .build()

        workManager.enqueue(saveWorkRequest)

        oldManifestationModel = manifestation

    }


    fun scheduleSaveManifestationWorker(manifestation: ManifestationModel, context: Context) {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(ManifestationModel::class.java)
        val manifestationJson = jsonAdapter.toJson(manifestation)

        val inputWorkData = workDataOf(
            "REGISTER" to manifestationJson
        )

        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        val saveWorkRequest = PeriodicWorkRequestBuilder<SaveManifestationWorker>(
            1, TimeUnit.MINUTES, // repeatInterval (the period cycle)
            1, TimeUnit.MINUTES) // flexInterval
            .setInputData(inputWorkData)
            .setConstraints(constraints)
            .setInitialDelay(35, TimeUnit.SECONDS)
            .addTag(tag = MANIFESTATION_TAG)
            .build()

        workManager.enqueue(saveWorkRequest)

    }
}