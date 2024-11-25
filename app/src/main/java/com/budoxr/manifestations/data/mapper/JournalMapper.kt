package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.JournalEntity
import java.util.Date

fun emptyJournalEntity() =
    JournalEntity(
        id = null,
        lessonDay = 0,
        manifestationId = 0,
        question = 0,
        answer = "",
        responseDate = Date().toFechaTimeDb()
    )


