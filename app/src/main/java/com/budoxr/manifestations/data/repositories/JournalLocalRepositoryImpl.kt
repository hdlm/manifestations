package com.budoxr.manifestations.data.repositories

import com.budoxr.manifestations.data.database.daos.JournalDao
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithJournals
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class JournalLocalRepositoryImpl : JournalLocalRepository, KoinComponent {
    private val journalDao : JournalDao by inject()

    override suspend fun allJournals(manifestationId: Int): List<ManifestationWithJournals> =
        journalDao.getAllJournalsByManifestation(manifestationId)

    override fun allJournalsFlow(): Flow<List<ManifestationWithJournals>> =
        journalDao.observeAllJournals()

    override suspend fun getLastId(): Int =
        journalDao.getLastId()

    override suspend fun journalAnswerExist(
        lessonDay: Int,
        manifestationId: Int,
        question: Int
    ): Int? =
        journalDao.journalAnswerExist(lessonDay, manifestationId, question)


    override suspend fun insert(journal: JournalEntity) {
        journalDao.insertJournal(journal)
    }

    override suspend fun insertAll(journals: List<JournalEntity>) {
        journalDao.insertAllJournal(journals)
    }

    override suspend fun delete(journal: JournalEntity) {
        journalDao.deleteJournal(journal)
    }

    override suspend fun delete(journalId: Int) {
        journalDao.deleteJournalById(journalId)
    }

}