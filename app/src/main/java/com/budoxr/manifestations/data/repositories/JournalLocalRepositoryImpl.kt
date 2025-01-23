package com.budoxr.manifestations.data.repositories

import com.budoxr.manifestations.data.database.daos.JournalDao
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class JournalLocalRepositoryImpl : JournalLocalRepository, KoinComponent {
    private val journalDao : JournalDao by inject()

    override suspend fun allJournals(lessonId: Int): List<LessonWithJournals> =
        journalDao.getAllJournals(lessonId)

    override fun allJournalsFlow(lessonId: Int): Flow<List<LessonWithJournals>> =
        journalDao.observeAllJournals(lessonId)


    override suspend fun journalAnswerExist(
        lessonDay: Int,
        question: Int
    ): Int =
        journalDao.journalAnswerExist(lessonDay, question)


    override suspend fun insert(journal: JournalEntity) {
        journalDao.insertJournal(journal)
    }

    override suspend fun insertAll(journals: List<JournalEntity>) {
        journalDao.insertAllJournal(journals)
    }

    override suspend fun delete(journal: JournalEntity) {
        journalDao.deleteJournal(journal)
    }

    override suspend fun deleteAll(journals: List<JournalEntity>) {
        journalDao.deleteJournals(journals)
    }

}