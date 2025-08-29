package com.budoxr.manifestations.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.budoxr.manifestations.data.database.daos.ManifestationDao
import com.budoxr.manifestations.data.database.daos.ConfigDao
import com.budoxr.manifestations.data.database.daos.JournalDao
import com.budoxr.manifestations.data.database.daos.LessonDao
import com.budoxr.manifestations.data.database.entities.ConfigEntity
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.ManifestationEntity

@Database(entities = [
    ManifestationEntity::class,
    LessonEntity::class,
    JournalEntity::class,
    ConfigEntity::class,
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun manifestationDao(): ManifestationDao
    abstract fun lessonDao(): LessonDao
    abstract fun journalDao(): JournalDao
    abstract fun configDao(): ConfigDao
}