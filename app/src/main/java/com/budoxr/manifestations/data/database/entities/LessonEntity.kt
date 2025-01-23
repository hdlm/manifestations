package com.budoxr.manifestations.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "lesson",
    foreignKeys = [
        ForeignKey(entity = ManifestationEntity::class,
            parentColumns = ["id"],
            childColumns = ["manifestation_id"],
            onDelete = NO_ACTION
        )
    ],
    indices = [
        Index("manifestation_id")
    ]
)
data class LessonEntity (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val day: Int,
    val subject: String,
    @ColumnInfo("manifestation_id") val manifestationId: Int,
)