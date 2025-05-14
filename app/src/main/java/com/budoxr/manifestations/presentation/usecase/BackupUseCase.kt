package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BackupUseCase : KoinComponent {
    private val localRepository: LocalStorage by inject()

    suspend operator fun invoke( manifestations: List<ManifestationModel>) {
        localRepository.backupDatabase(manifestations)

    }

}