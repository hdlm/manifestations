package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LessonLastRecordUseCase : KoinComponent {
    private val localRepository: LessonLocalRepository
        get() = get()

    suspend operator fun invoke(manifestationId: Int): LessonEntity? =
        localRepository.getLastLesson(manifestationId)

}