package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ManifestationLastIdUseCase : KoinComponent {
    private val localRepository: ManifestationLocalRepository
        get() = get()

    suspend fun invoke(): Int = localRepository.getLastId()

}