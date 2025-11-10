package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.commons.fromFechaTimeDb
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import java.util.Date


fun ManifestationModel.toEntity() =
    ManifestationEntity(
        id = id,
        overview = overview,
        description = description,
        startDate = startDate,
        dueDate = dueDate,
        category = category,
    )

fun ManifestationEntity.toModel() =
    ManifestationModel(
        id = id,
        overview = overview,
        description = description,
        startDate = startDate,
        dueDate = startDate,
        category = category,
    )


fun emptyManifestationModel() =
    ManifestationModel(
        id = null,
        overview = "",
        description = "",
        startDate = Date().toFechaTimeDb(),
        dueDate = Date().toFechaTimeDb(),
        category = "",
    )
