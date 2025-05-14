package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LessonInfoUseCase : KoinComponent {
    private val localRepository: LessonLocalRepository
        get() = get()

    operator fun invoke(): Flow<List<ManifestationWithLessons>> =
        localRepository.allLessonsFlow()

    suspend operator fun invoke(isSynchronized: Boolean): List<ManifestationWithLessons> =
        localRepository.allLessons()


    operator fun invoke(manifestationId: Int): Flow<List<ManifestationWithLessons>> =
        localRepository.allLessonsByManifestationIdFlow(manifestationId)

    suspend operator fun invoke(manifestationId: Int, isSynchronized: Boolean): List<ManifestationWithLessons> =
        localRepository.allLessonsByManifestationId(manifestationId)

}