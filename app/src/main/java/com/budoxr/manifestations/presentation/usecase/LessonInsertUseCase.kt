package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LessonInsertUseCase : KoinComponent {
    private val localRepository: LessonLocalRepository
        get() = get()

    suspend operator fun invoke(lesson: LessonEntity): Int =
            localRepository.insert(lesson).toInt()

    suspend operator fun invoke(lessons: List<LessonEntity>): Unit {
        localRepository.insertAll(lessons)
    }

}