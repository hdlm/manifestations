package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.data.repositories.ConfigLocalRepository
import com.budoxr.manifestations.presentation.domain.ConfigModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigInsertUseCase : KoinComponent {
    private val repository: ConfigLocalRepository by inject()

    suspend operator fun invoke(config: ConfigModel) : Unit =
        repository.insert(config.toEntity())


}