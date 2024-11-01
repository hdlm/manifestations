package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import kotlinx.coroutines.flow.Flow

interface ManifestationLocalRepository {

    fun allManifestationsFlow(): Flow<List<ManifestationEntity>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allManifestations(): List<ManifestationEntity>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(manifestation: ManifestationEntity)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(manifestation: ManifestationEntity)

}