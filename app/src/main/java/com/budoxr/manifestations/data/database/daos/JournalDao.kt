package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithJournals
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllJournal(journals: List<JournalEntity>)

    @Delete
    suspend fun deleteJournal(journal: JournalEntity)

    @Query("DELETE FROM journal WHERE id = :journalId")
    suspend fun deleteJournalById(journalId: Int)

    @Query(
        """
            SELECT
                manifestation.*
            FROM manifestation
            INNER JOIN journal ON manifestation.id = journal.manifestation_id
            ORDER BY journal.lesson_day, journal.question ASC
        """
    )
    fun observeAllJournals(): Flow<List<ManifestationWithJournals>>


    @Query(
        """
            SELECT
                manifestation.*
            FROM manifestation
            INNER JOIN journal ON manifestation.id = journal.manifestation_id
            WHERE manifestation.id = :manifestationId ORDER BY journal.lesson_day, journal.question ASC
        """
    )
    suspend fun getAllJournalsByManifestation(manifestationId: Int): List<ManifestationWithJournals>


    @Query("SELECT MAX(COALESCE(id, 0)) FROM journal")
    suspend fun getLastId(): Int

    @Query(
        """
           SELECT id FROM journal WHERE lesson_day = :lessonDay 
                AND manifestation_id = :manifestationId 
                AND question = :question 
        """
    )
    suspend fun journalAnswerExist(lessonDay: Int, manifestationId: Int, question: Int): Int?


}