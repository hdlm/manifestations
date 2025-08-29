package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonAndJournal
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import kotlinx.coroutines.flow.Flow

interface JournalLocalRepository {

    fun allJournalsFlow(manifestationId: Int): Flow<List<LessonWithJournals>>

    fun allLessonsAndJournalsFlow(manifestationId: Int): Flow<List<LessonAndJournal?>>

    @WorkerThread
    suspend fun insert(journal: JournalEntity): Long

    @WorkerThread
    suspend fun insertAll(journals: List<JournalEntity>)

    @WorkerThread
    suspend fun delete(journal: JournalEntity)

    @WorkerThread
    suspend fun deleteAll(journals: List<JournalEntity>)

}