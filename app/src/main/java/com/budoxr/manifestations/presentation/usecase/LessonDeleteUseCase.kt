package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LessonDeleteUseCase : KoinComponent {
    private val localRepository: LessonLocalRepository
        get() = get()

    suspend operator fun invoke(lesson: LessonEntity): Unit {
        localRepository.delete(lesson)
    }

    suspend operator fun invoke(lessons: List<LessonEntity>): Unit {
        localRepository.deleteAll(lessons)
    }
}