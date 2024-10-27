package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.presentation.domain.LessonModel

fun emptyLessonModel() =
    LessonModel(
        day = 0,
        subject = "",
        summary = emptyList(),
        journal = emptyList(),
        meditation = null
    )