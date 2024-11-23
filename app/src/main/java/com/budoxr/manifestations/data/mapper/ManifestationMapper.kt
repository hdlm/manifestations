package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import java.util.Date

fun ManifestationModel.copy(id: Int?) =
    ManifestationModel(
        id = id ?: this.id,
        overview = overview,
        description = description,
        creationDate = creationDate,
        dueDate = dueDate,
        category = category
    )

fun ManifestationModel.toEntity() =
    ManifestationEntity(
        id = id,
        overview = overview,
        description = description,
        creationDate = creationDate,
        dueDate = dueDate,
        category = category,
    )

fun ManifestationEntity.toModel() =
    ManifestationModel(
        id = id,
        overview = overview,
        description = description,
        creationDate = creationDate,
        dueDate = dueDate,
        category = category,
    )


fun emptyManifestationModel() =
    ManifestationModel(
        id = null,
        overview = "",
        description = "",
        creationDate = Date().toFechaTimeDb(),
        dueDate = Date().toFechaTimeDb(),
        category = "",
    )
