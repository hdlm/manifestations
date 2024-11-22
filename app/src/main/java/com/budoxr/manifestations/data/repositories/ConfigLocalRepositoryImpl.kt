package com.budoxr.manifestations.data.repositories

import com.budoxr.manifestations.data.database.daos.ConfigDao
import com.budoxr.manifestations.data.database.entities.ConfigEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigLocalRepositoryImpl : ConfigLocalRepository, KoinComponent {

    private val configDao : ConfigDao by inject()

    override suspend fun getConfig(id: Int): ConfigEntity? =
        configDao.getConfig()


    override suspend fun insert(config: ConfigEntity) {
        configDao.insertConfig(config = config)
    }

}