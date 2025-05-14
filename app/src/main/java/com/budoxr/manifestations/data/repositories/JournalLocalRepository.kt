package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import kotlinx.coroutines.flow.Flow

interface JournalLocalRepository {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allJournals(lessonId: Int): List<LessonWithJournals>

    fun allJournalsFlow(manifestationId: Int): Flow<List<LessonWithJournals>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getJournalByQuestion(questionIdx: Int, lessonId: Int): JournalEntity?

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(journal: JournalEntity): Long

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(journals: List<JournalEntity>)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(journal: JournalEntity)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(journals: List<JournalEntity>)

}