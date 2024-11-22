package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ManifestationInsertUseCase : KoinComponent {
    private val repository: ManifestationLocalRepository by inject()

    suspend operator fun invoke(manifestation: ManifestationEntity) : Unit =
        repository.insert(manifestation)

    suspend operator fun invoke(manifestations: List<ManifestationEntity>) : Unit =
        repository.insertAll(manifestations)


}