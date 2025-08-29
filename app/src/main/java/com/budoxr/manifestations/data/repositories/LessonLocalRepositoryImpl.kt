package com.budoxr.manifestations.data.repositories

import com.budoxr.manifestations.data.database.daos.LessonDao
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationAndLesson
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LessonLocalRepositoryImpl : LessonLocalRepository, KoinComponent {
    private val lessonDao : LessonDao by inject()


    override fun allLessonsFlow(): Flow<List<ManifestationWithLessons>> =
        lessonDao.observeAllLessons()

    override fun allLessonsByManifestationIdFlow(manifestationId: Int): Flow<List<ManifestationWithLessons>> =
        lessonDao.observeAllLessonsByManifestationId(manifestationId)

    override fun allLessonsByDayFlow(manifestationId: Int, day: Int): Flow<ManifestationAndLesson?> =
        lessonDao.observeManifestationAndLessonsForDay(manifestationId, day)


    override fun countLessonsByManifestationIdFlow(manifestationId: Int): Flow<Int> =
        lessonDao.observeCountByManifestationId(manifestationId)


    override fun getLastLessonFlow(manifestationId: Int): Flow<LessonEntity?> =
        lessonDao.observeLastLesson(manifestationId)

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