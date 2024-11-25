package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithJournals
import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class JournalInfoUseCase : KoinComponent {
    private val localRepository: JournalLocalRepository
        get() = get()

    operator fun invoke(): Flow<List<ManifestationWithJournals>> =
        localRepository.allJournalsFlow()

}