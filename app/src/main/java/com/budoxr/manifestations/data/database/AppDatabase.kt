package com.budoxr.manifestations.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.budoxr.manifestations.data.database.daos.ManifestationDao
import com.budoxr.manifestations.data.database.entities.ManifestationEntity

@Database(entities = [
    ManifestationEntity::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun manifestationDao(): ManifestationDao
}