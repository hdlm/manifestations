package com.budoxr.manifestations.data.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import java.util.Objects

class ManifestationWithLessonsAndJournals {
    @Embedded
    lateinit var manifestation: ManifestationEntity

    @Relation(
        parentColumn = "id",
        entityColumn = "manifestation_id",
        entity = LessonEntity::class
    )
    lateinit var _lessons: List<LessonWithJournals>


    /**
     * Allow consumers to destructure this class
     */
    operator fun component1(): ManifestationEntity = manifestation
    operator fun component2(): List<LessonWithJournals> = _lessons

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ManifestationWithLessonsAndJournals -> manifestation == other.manifestation && _lessons == other._lessons
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(manifestation, _lessons)

}