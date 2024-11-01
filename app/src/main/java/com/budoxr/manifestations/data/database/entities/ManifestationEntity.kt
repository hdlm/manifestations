package com.budoxr.manifestations.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manifestation")
data class ManifestationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val overview: String,
    val description: String,
    @ColumnInfo(name = "creation_date")
    val creationDate: String,
    @ColumnInfo(name = "due_date")
    val dueDate: String,
    val category: String,
)
