package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budoxr.manifestations.data.database.entities.JournalEntity
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
    
    @Query(
        """
            SELECT 
                lesson.*
            FROM lesson 
            INNER JOIN journal ON lesson.id = journal.lesson_id 
            WHERE lesson.manifestation_id = :manifestationId
            ORDER BY lesson.day, journal.question_idx ASC
        """
    )
    fun observeAllJournals(manifestationId: Int): Flow<List<LessonWithJournals>>

    @Query(
        """
            SELECT 
                lesson.*
            FROM lesson 
            INNER JOIN journal ON lesson.id = journal.lesson_id 
            WHERE lesson.manifestation_id = :manifestationId
            ORDER BY lesson.day, journal.question_idx ASC
        """
    )
    suspend fun getAllJournals(manifestationId: Int): List<LessonWithJournals>


    @Query(
        """
           SELECT * FROM journal WHERE lesson_id = :lessonId 
                AND question_idx = :questionIdx 
        """
    )
    suspend fun getJournalByQuestion(lessonId: Int, questionIdx: Int): JournalEntity?


}