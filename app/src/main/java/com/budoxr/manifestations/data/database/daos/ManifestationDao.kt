package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Manifestation table.
 */
@Dao
interface ManifestationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManifestation(manifestation: ManifestationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllManifestation(manifestations: List<ManifestationEntity>)

    @Delete
    suspend fun deleteManifestation(manifestation: ManifestationEntity)

    /**
     * Observes all manifestations from the database.
     * The returned Flow will emit a new list whenever the data changes.
     */
    @Transaction
    @Query("SELECT * FROM manifestation ORDER BY id DESC")
    fun observeAllManifestations(): Flow<List<ManifestationEntity>>

    /**
     * Observes the last ID from the database.
     * This is useful for knowing the last inserted ID in a reactive way.
     */
    @Transaction
    @Query("SELECT * FROM manifestation ORDER by id DESC LIMIT 1")
    fun observeLastManifestation(): Flow<ManifestationEntity?>


    @Transaction
    @Query("SELECT * FROM manifestation ORDER by id DESC LIMIT 1")
    suspend fun getLastManifestation(): ManifestationEntity?
}