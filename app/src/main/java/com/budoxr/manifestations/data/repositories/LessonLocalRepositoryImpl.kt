package com.budoxr.manifestations.data.repositories

import com.budoxr.manifestations.data.database.daos.LessonDao
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LessonLocalRepositoryImpl : LessonLocalRepository, KoinComponent {
    private val lessonDao : LessonDao by inject()

    override suspend fun allLessons(): List<ManifestationWithLessons> =
        lessonDao.getAllLessons()

    override fun allLessonsFlow(): Flow<List<ManifestationWithLessons>> =
        lessonDao.observeAllLessons()

    override suspend fun allLessonsByManifestationId(manifestationId: Int): List<ManifestationWithLessons> =
        lessonDao.getAllLessonsByManifestationId(manifestationId)

    override fun allLessonsByManifestationIdFlow(manifestationId: Int): Flow<List<ManifestationWithLessons>> =
        lessonDao.observeAllLessonsByManifestationId(manifestationId)


    override suspend fun getLessonsByDay(day: Int, manifestationId: Int): LessonEntity? =
        lessonDao.getLessonByDay(day = day, manifestationId = manifestationId)

    override suspend fun countLessonsByManifestationId(manifestationId: Int): Int =
        lessonDao.countByManifestationId(manifestationId)


    override suspend fun getLastLesson(manifestationId: Int): LessonEntity? =
        lessonDao.getLastLesson(manifestationId)

    override suspend fun insert(lesson: LessonEntity): Long  =
        lessonDao.insertLesson(lesson)

    override suspend fun insertAll(lessons: List<LessonEntity>) {
        lessonDao.insertLessons(lessons)
    }

    override suspend fun delete(lesson: LessonEntity) {
        lessonDao.deleteLesson(lesson)
    }

    override suspend fun deleteAll(lessons: List<LessonEntity>) {
        lessonDao.deleteLessons(lessons)
    }

}