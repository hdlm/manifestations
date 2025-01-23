package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.JournalEntity
import java.util.Date

fun emptyJournalEntity() =
    JournalEntity(
        id = null,
        lessonId = 0,
        questionIdx = 0,
        questionSlug = null,
        answer = "",
        responseDate = Date().toFechaTimeDb()
    )


