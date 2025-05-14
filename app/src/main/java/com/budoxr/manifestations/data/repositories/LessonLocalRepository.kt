package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import kotlinx.coroutines.flow.Flow

interface LessonLocalRepository {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allLessons(): List<ManifestationWithLessons>

    fun allLessonsFlow(): Flow<List<ManifestationWithLessons>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun allLessonsByManifestationId(manifestationId: Int): List<ManifestationWithLessons>

    fun allLessonsByManifestationIdFlow(manifestationId: Int): Flow<List<ManifestationWithLessons>>

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLessonsByDay(day: Int, manifestationId: Int): LessonEntity?

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun countLessonsByManifestationId(manifestationId: Int): Int

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLastLesson(manifestationId: Int): LessonEntity?

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(lesson: LessonEntity): Long


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