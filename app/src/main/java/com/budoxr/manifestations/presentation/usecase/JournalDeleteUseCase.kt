package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class JournalDeleteUseCase : KoinComponent {
    private val repository: JournalLocalRepository by inject()

    suspend operator fun invoke(journal: JournalEntity) {
        repository.delete(journal)
    }

    suspend operator fun invoke(journals: List<JournalEntity>) {
        repository.deleteAll(journals)
    }

}