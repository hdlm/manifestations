package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.LessonLocalRepository
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

    suspend operator fun invoke(manifestationId: Int): Int =
        lessonLocalRepository.countLessonsByManifestationId(manifestationId)

}