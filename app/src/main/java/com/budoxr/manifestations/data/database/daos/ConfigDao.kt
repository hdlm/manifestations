package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budoxr.manifestations.data.database.entities.ConfigEntity

@Dao
interface ConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Query("SELECT * FROM config")
    suspend fun getConfig(): ConfigEntity?

}