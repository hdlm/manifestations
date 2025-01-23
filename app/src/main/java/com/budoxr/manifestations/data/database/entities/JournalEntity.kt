package com.budoxr.manifestations.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "journal",
    foreignKeys = [
        ForeignKey(entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = NO_ACTION )
    ],
    indices = [
        Index("lesson_id")
    ]
)
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "lesson_id") val lessonId: Int,
    @ColumnInfo(name = "question_idx") val questionIdx: Int,
    @ColumnInfo(name = "question_slug") val questionSlug: String?,
    @ColumnInfo(name = "answer") val answer: String?,
    @ColumnInfo(name = "response_date") val responseDate: String,
)
