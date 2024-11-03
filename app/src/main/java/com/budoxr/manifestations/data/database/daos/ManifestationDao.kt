package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ManifestationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManifestation(manifestation: ManifestationEntity)

    @Delete
    suspend fun deleteManifestation(manifestation: ManifestationEntity)

    @Query("SELECT * FROM manifestation")
    suspend fun getAllManifestations(): List<ManifestationEntity>

    @Query("SELECT * FROM manifestation")
    fun observeAllManifestations(): Flow<List<ManifestationEntity>>

}