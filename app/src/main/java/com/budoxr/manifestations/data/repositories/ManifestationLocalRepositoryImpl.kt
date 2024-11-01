package com.budoxr.manifestations.data.repositories

import com.budoxr.manifestations.data.database.AppDatabase
import com.budoxr.manifestations.data.database.daos.ManifestationDao
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ManifestationLocalRepositoryImpl : ManifestationLocalRepository, KoinComponent {

    private val manifestationDao : ManifestationDao by inject()

    override fun allManifestationsFlow(): Flow<List<ManifestationEntity>> =
        manifestationDao.observeAllManifestations()

    override suspend fun allManifestations(): List<ManifestationEntity> =
        manifestationDao.getAllManifestations()

    override suspend fun insert(manifestation: ManifestationEntity) =
        manifestationDao.insertManifestation(manifestation)

    override suspend fun delete(manifestation: ManifestationEntity) =
        manifestationDao.deleteManifestation(manifestation)

}