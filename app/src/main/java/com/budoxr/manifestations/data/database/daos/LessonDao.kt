package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)

    @Delete
    suspend fun deleteLessons(lessons: List<LessonEntity>)

    @Query(
        """
            SELECT 
                manifestation.*
            FROM manifestation 
            INNER JOIN lesson ON manifestation.id = lesson.manifestation_id 
            ORDER BY lesson.day ASC
        """
    )
    fun observeAllLessons(): Flow<List<ManifestationWithLessons>>

    @Query(
        """
            SELECT 
                manifestation.*
            FROM manifestation 
            INNER JOIN lesson ON manifestation.id = lesson.manifestation_id 
            ORDER BY lesson.day ASC
        """
    )
    suspend fun getAllLessons(): List<ManifestationWithLessons>

    @Query(
        """
            SELECT * FROM manifestation 
            WHERE manifestation.id = :manifestationId
        """
    )
    fun observeAllLessonsByManifestationId(manifestationId: Int): Flow<List<ManifestationWithLessons>>

    @Query(
        """
            SELECT * FROM manifestation 
            WHERE manifestation.id = :manifestationId
        """
    )
    suspend fun getAllLessonsByManifestationId(manifestationId: Int): List<ManifestationWithLessons>


    @Query("SELECT * FROM lesson WHERE manifestation_id = :manifestationId AND day = :day")
    suspend fun getLessonByDay(day: Int, manifestationId: Int): LessonEntity?

    @Query("SELECT COUNT(*) FROM lesson WHERE manifestation_id = :manifestationId")
    suspend fun countByManifestationId(manifestationId: Int): Int

    @Query("SELECT * FROM lesson WHERE manifestation_id = :manifestationId ORDER BY id DESC LIMIT 1")
    suspend fun getLastLesson(manifestationId: Int): LessonEntity?

}
