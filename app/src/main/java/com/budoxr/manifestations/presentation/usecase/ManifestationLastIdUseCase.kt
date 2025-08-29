package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ManifestationLastIdUseCase : KoinComponent {
    private val localRepository: ManifestationLocalRepository
        get() = get()

    operator fun invoke(): Flow<Int> = localRepository.getLastIdFlow()

}