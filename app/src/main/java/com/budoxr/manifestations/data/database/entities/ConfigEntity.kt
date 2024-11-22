package com.budoxr.manifestations.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey val id: Int,
    val language: String,
    val country: String,
    @ColumnInfo(name = "speech_rate") val speechRate: Float,
)
