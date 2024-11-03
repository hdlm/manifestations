package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ManifestationInsertUseCase : KoinComponent {
    private val repository: ManifestationLocalRepository by inject()

    suspend operator fun invoke(manifestation: ManifestationEntity) : Unit =
        repository.insert(manifestation)

}