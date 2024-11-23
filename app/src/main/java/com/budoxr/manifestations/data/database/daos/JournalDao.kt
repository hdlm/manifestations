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

    @Query(
        """
            SELECT
                manifestation.*
            FROM manifestation
            INNER JOIN journal ON manifestation.id = journal.manifestation_id
            WHERE manifestation.id = :manifestationId ORDER BY journal.lesson_day, journal.question ASC
        """
    )
    fun observeAllJournalsByManifestation(manifestationId: Int): Flow<List<ManifestationWithJournals>>


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


}