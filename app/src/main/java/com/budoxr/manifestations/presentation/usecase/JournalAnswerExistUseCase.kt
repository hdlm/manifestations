package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class JournalAnswerExistUseCase : KoinComponent {
    private val localRepository: JournalLocalRepository
        get() = get()

    /**
     * The function checks if a journal answer already exists in the local repository.
     * @return the _id_ that matched or null
     */
    suspend operator fun invoke(lessonDay: Int, question: Int) =
        localRepository.journalAnswerExist(lessonDay, question)

}