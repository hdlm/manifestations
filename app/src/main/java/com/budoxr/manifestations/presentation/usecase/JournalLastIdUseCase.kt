package com.budoxr.manifestations.presentation.usecase

import com.budoxr.manifestations.data.repositories.JournalLocalRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class JournalLastIdUseCase : KoinComponent {
    private val localRepository: JournalLocalRepository
        get() = get()

    suspend operator fun invoke(): Int = localRepository.getLastId()
}