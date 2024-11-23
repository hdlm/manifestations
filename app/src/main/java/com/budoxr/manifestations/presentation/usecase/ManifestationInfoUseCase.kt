package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.data.mapper.toModel
import com.budoxr.manifestations.data.repositories.ManifestationLocalRepository
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ManifestationInfoUseCase : KoinComponent {
    private val localRepository: ManifestationLocalRepository
        get() = get()

    operator fun invoke() : Flow<List<ManifestationModel>> =
        localRepository.allManifestationsFlow()
            .map { manifestations ->
                val models : MutableSet<ManifestationModel> = mutableSetOf()
                manifestations.forEach { entity ->
                    models.add(entity.toModel())
                }
                models.toList()
            }

    suspend operator fun invoke(scope: CoroutineScope) : List<ManifestationEntity> =
        localRepository.allManifestations()

}