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

    operator fun invoke(manifestationId: Int): Flow<List<ManifestationWithLessons>> =
        localRepository.allLessonsByManifestationIdFlow(manifestationId)

}