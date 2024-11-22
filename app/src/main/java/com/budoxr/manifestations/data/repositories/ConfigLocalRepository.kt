package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.ConfigEntity

interface ConfigLocalRepository {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getConfig(id: Int): ConfigEntity?

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(config: ConfigEntity)

}