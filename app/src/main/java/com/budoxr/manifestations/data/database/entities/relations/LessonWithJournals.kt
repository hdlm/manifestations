package com.budoxr.manifestations.data.database.entities.relations

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import java.util.Objects

class LessonWithJournals {
    @Embedded
    lateinit var lesson: LessonEntity

    @Relation(
        parentColumn = "id",
        entityColumn = "lesson_id",
        entity = JournalEntity::class
    )
    lateinit var _journals: List<JournalEntity>

    @get:Ignore
    val journal : JournalEntity
        get() = _journals.last()

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1(): LessonEntity = lesson
    operator fun component2(): List<JournalEntity> = _journals

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is LessonWithJournals -> lesson == other.lesson && _journals == other._journals
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(lesson, _journals)

}