package com.budoxr.manifestations.data.database.entities.relations

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import java.util.Objects

class ManifestationWithLessons {
    @Embedded
    lateinit var manifestation: ManifestationEntity

    @Relation(
        parentColumn = "id",
        entityColumn = "manifestation_id",
    )
    lateinit var _lessons: List<LessonEntity>

    @get:Ignore
    val lesson: LessonEntity
        get() = _lessons.first()

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1(): ManifestationEntity = manifestation
    operator fun component2(): List<LessonEntity> = _lessons

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ManifestationWithLessons -> manifestation == other.manifestation && _lessons == other._lessons
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(manifestation, _lessons)

}