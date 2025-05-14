package com.budoxr.manifestations.presentation.usecase

import android.net.Uri
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImportDatabaseUseCase : KoinComponent {
    private val localRepository: LocalStorage by inject()

    suspend operator fun invoke(selectedFolderUri: Uri) : List<ManifestationModel> =
        localRepository.importDatabase(selectedFolderUri)

}