package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.repositories.LessonLocalRepository
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LessonInsertUseCase : KoinComponent {
    private val localRepository: LessonLocalRepository
        get() = get()

    private val util: Utily
        get() = get()

    suspend operator fun invoke(lesson: LessonEntity, scope: CoroutineScope): Unit {
        if (lesson.id == null) {
            val record = util.performAsyncOperation(scope = scope) {
                localRepository.getLessonsByDay(day = lesson.day, manifestationId = lesson.manifestationId)
            }.await()
            record?.let {
                val newLesson = record.copy(id = it.id)
                localRepository.insert(newLesson)
            } ?: run {
                localRepository.insert(lesson)
            }
        } else {
            localRepository.insert(lesson)
        }

    }

    suspend operator fun invoke(lessons: List<LessonEntity>): Unit {
        localRepository.insertAll(lessons)
    }

}