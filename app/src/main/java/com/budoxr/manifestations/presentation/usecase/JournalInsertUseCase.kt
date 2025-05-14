package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class JournalInsertUseCase : KoinComponent {
    private val localRepository: JournalLocalRepository
        get() = get()

    suspend operator fun invoke(journal: JournalEntity) : Int =
        localRepository.insert(journal).toInt()

    suspend operator fun invoke(journals: List<JournalEntity>): Unit {
        localRepository.insertAll(journals)

    }

}