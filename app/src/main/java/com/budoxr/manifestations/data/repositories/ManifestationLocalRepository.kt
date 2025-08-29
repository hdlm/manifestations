package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import kotlinx.coroutines.flow.Flow

interface ManifestationLocalRepository {

    fun allManifestationsFlow(): Flow<List<ManifestationEntity>>

    fun getLastIdFlow(): Flow<Int>

    @WorkerThread
    suspend fun insert(manifestation: ManifestationEntity)

    @WorkerThread
    suspend fun insertAll(manifestations: List<ManifestationEntity>)

    @WorkerThread
    suspend fun delete(manifestation: ManifestationEntity)

}