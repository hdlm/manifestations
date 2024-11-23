package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithJournals
import kotlinx.coroutines.flow.Flow

interface JournalLocalRepository {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allJournals(manifestationId: Int): List<ManifestationWithJournals>

    fun allJournalsFlow(manifestationId: Int): Flow<List<ManifestationWithJournals>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(journal: JournalEntity)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(journals: List<JournalEntity>)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(journal: JournalEntity)

}