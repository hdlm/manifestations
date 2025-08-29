package com.budoxr.manifestations.data.repositories

import androidx.annotation.WorkerThread
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationAndLesson
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import kotlinx.coroutines.flow.Flow

interface LessonLocalRepository {


    fun allLessonsFlow(): Flow<List<ManifestationWithLessons>>

    fun allLessonsByManifestationIdFlow(manifestationId: Int): Flow<List<ManifestationWithLessons>>

    fun allLessonsByDayFlow(manifestationId: Int, day: Int): Flow<ManifestationAndLesson?>

    fun countLessonsByManifestationIdFlow(manifestationId: Int): Flow<Int>

    fun getLastLessonFlow(manifestationId: Int): Flow<LessonEntity?>

    @WorkerThread
    suspend fun insert(lesson: LessonEntity): Long

    @WorkerThread
    suspend fun insertAll(lessons: List<LessonEntity>)

    @WorkerThread
    suspend fun delete(lesson: LessonEntity)

    @WorkerThread
    suspend fun deleteAll(lessons: List<LessonEntity>)





}