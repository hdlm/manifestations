package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import kotlinx.coroutines.flow.Flow

interface JournalLocalRepository {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allJournals(lessonId: Int): List<LessonWithJournals>

    fun allJournalsFlow(lessonId: Int): Flow<List<LessonWithJournals>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun journalAnswerExist(lessonDay: Int, question: Int): Int?

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(journal: JournalEntity)

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