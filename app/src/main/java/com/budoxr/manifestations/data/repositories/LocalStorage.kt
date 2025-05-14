package com.budoxr.manifestations.data.repositories

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import kotlinx.coroutines.flow.Flow
import java.io.File

abstract class LocalStorage(val context: Context) {

    abstract fun loadFileStreamFromAssets(filename: String): File
    abstract fun loadFileReaderFromAssets(filename: String): String
    abstract fun getLessons(): Flow<LessonsWrapper>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    abstract suspend fun backupDatabase(manifestations: List<ManifestationModel>) : Unit

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    abstract suspend fun restoreDatabase() : List<ManifestationModel>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    abstract suspend fun exportDatabase(manifestations: List<ManifestationModel>, selectedFolderUri: Uri) : Unit

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    abstract suspend fun importDatabase(selectedFolderUri: Uri) : List<ManifestationModel>

}