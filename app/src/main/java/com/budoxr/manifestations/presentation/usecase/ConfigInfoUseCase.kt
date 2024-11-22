package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.mapper.emptyConfigModel
import com.budoxr.manifestations.data.mapper.toModel
import com.budoxr.manifestations.data.repositories.ConfigLocalRepository
import com.budoxr.manifestations.presentation.domain.ConfigModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ConfigInfoUseCase : KoinComponent {
    private val localRepository: ConfigLocalRepository
        get() = get()

    suspend operator fun invoke() : ConfigModel =
        localRepository.getConfig(1)?.toModel() ?: emptyConfigModel()

}