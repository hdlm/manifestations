package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonAndJournal
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllJournal(journals: List<JournalEntity>)

    @Delete
    suspend fun deleteJournal(journal: JournalEntity)

    @Delete
    suspend fun deleteJournals(journals: List<JournalEntity>)

    @Transaction
    @Query(
        "SELECT * FROM lesson WHERE manifestation_id = :manifestationId ORDER BY day"
    )
    fun observeAllJournals(manifestationId: Int): Flow<List<LessonWithJournals>>

    @Transaction
    @Query(
        """
            SELECT
                lesson.id AS lesson_id, lesson.day AS lesson_day, lesson.subject AS lesson_subject, lesson.manifestation_id AS lesson_manifestation_id,
                journal.id AS journal_id, journal.lesson_id AS journal_lesson_id, journal.question_idx AS journal_question_idx, journal.question_slug AS journal_question_slug, journal.answer AS journal_answer, journal.response_date AS journal_response_date
            FROM lesson
            INNER JOIN journal ON lesson.id = journal.lesson_id
            WHERE lesson.manifestation_id = :manifestationId
            ORDER BY lesson.day, journal.question_idx ASC
        """
    )
    fun observeLessonAndJournal(manifestationId: Int): Flow<List<LessonAndJournal>>

}