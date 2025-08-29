package com.budoxr.manifestations.data.database.entities.relations

import androidx.room.Embedded
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.ManifestationEntity

data class ManifestationAndLesson(
    @Embedded(prefix = "manifestation_")
    val manifestation: ManifestationEntity,

    @Embedded(prefix = "lesson_")
    val lesson: LessonEntity
)
