package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LessonLastRecordUseCase : KoinComponent {
    private val localRepository: LessonLocalRepository
        get() = get()

    operator fun invoke(manifestationId: Int): Flow<LessonEntity?> =
        localRepository.getLastLessonFlow(manifestationId)

}