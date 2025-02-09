package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.data.database.entities.JournalEntity

fun emptyJournalEntity() =
    JournalEntity(
        id = null,
        lessonId = 0,
        questionIdx = 0,
        questionSlug = null,
        answer = "",
        responseDate = System.currentTimeMillis()
    )


