package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessonsAndJournals
import kotlinx.coroutines.flow.Flow

interface LessonLocalRepository {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allLessons(): List<ManifestationWithLessonsAndJournals>

    fun allLessonsFlow(): Flow<List<ManifestationWithLessonsAndJournals>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allLessonsByManifestationId(manifestationId: Int): List<ManifestationWithLessonsAndJournals>

    fun allLessonsByManifestationIdFlow(manifestationId: Int): Flow<List<ManifestationWithLessonsAndJournals>>


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(lesson: LessonEntity)


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(lessons: List<LessonEntity>)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(lesson: LessonEntity)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(lessons: List<LessonEntity>)





}