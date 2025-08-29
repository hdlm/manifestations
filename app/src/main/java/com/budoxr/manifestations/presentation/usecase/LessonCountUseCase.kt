package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * The class return the count of lessons associated to a specific manifestation
 * @param manifestationId the id of the manifestation
 * @return the count of lessons associated to a specific manifestation
 */
class LessonCountUseCase : KoinComponent  {
    private val lessonLocalRepository: LessonLocalRepository
        get() = get()

    operator fun invoke(manifestationId: Int): Flow<Int> =
        lessonLocalRepository.countLessonsByManifestationIdFlow(manifestationId)

}