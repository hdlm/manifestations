package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessonsAndJournals
import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class JournalInfoUseCase : KoinComponent {
    private val localRepository: JournalLocalRepository
        get() = get()

    operator fun invoke(lessonId: Int): Flow<List<LessonWithJournals>> =
        localRepository.allJournalsFlow(lessonId)

    suspend operator fun invoke(lessonId: Int, isSynchronized: Boolean): List<LessonWithJournals> =
        localRepository.allJournals(lessonId)

}