package com.budoxr.manifestations.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "journal",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(entity = ManifestationEntity::class,
            parentColumns = ["id"],
            childColumns = ["manifestation_id"],
            onDelete = NO_ACTION )
    ],
    indices = [
        Index("manifestation_id")
    ]
)
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val  id: Int?,
    @ColumnInfo(name = "lesson_day") val lessonDay: Int,
    @ColumnInfo(name = "manifestation_id") val manifestationId: Int,
    @ColumnInfo(name = "question") val question: Int,
    @ColumnInfo(name = "answer") val answer: String,
    @ColumnInfo(name = "response_date") val responseDate: String,

)

/*
@ForeignKey(entity = ParentEntity::class,
    parentColumns = ["id"],
    childColumns = ["parentId"],
    onDelete = NO_ACTION,
    onUpdate = CASCADE)

@Entity(tableName = "team",
    primaryKeys = ["team_id"],
    foreignKeys = [
        ForeignKey(entity = MatchEntity::class,
            parentColumns = ["match_id"],
            childColumns = ["match_id"],
            onDelete = NO_ACTION )
    ],
    indices = [
        Index("match_id")
    ]
)
data class TeamEntit
 */
