package com.budoxr.manifestations.data.database.entities.relations

import androidx.room.Embedded
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity

data class LessonAndJournal(
    @Embedded(prefix = "lesson_")
    val lesson: LessonEntity,

    @Embedded(prefix = "journal_")
    val journal: JournalEntity
)
