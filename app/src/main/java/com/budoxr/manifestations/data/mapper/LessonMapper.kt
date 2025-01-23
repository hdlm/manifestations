package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.presentation.domain.LessonModel


fun LessonModel.toEntity() =
    LessonEntity(
        id = id,
        day = day,
        subject = subject,
        manifestationId = manifestationId!!,
    )

fun emptyLessonModel() =
    LessonModel(
        id = null,
        manifestationId = null,
        day = 0,
        subject = "",
        summary = emptyList(),
        journal = emptyList(),
        meditation = null
    )