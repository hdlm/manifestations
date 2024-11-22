package com.budoxr.manifestations.data.repositories

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface LocalStorage {

    fun loadFileStreamFromAssets(context: Context, filename: String): File
    fun loadFileReaderFromAssets(context: Context, filename: String): String
    fun getLessons(context: Context): Flow<LessonsWrapper>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun backupDatabase(context: Context, manifestations: List<ManifestationModel>) : Unit

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun restoreDatabase(context: Context) : List<ManifestationModel>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun exportDatabase(context: Context, manifestations: List<ManifestationModel>, selectedFolderUri: Uri) : Unit

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun importDatabase(context: Context, selectedFolderUri: Uri) : List<ManifestationModel>

}