package com.budoxr.manifestations.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationAndLesson
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
// Replacing with @Relation room
//    @Query(
//        """
//            SELECT
//                manifestation.*
//            FROM manifestation
//            INNER JOIN lesson ON manifestation.id = lesson.manifestation_id
//            ORDER BY lesson.day ASC
//        """
//    )
//    fun observeAllLessons(): Flow<List<ManifestationWithLessons>>

    @Transaction
    @Query("SELECT * FROM manifestation")
    fun observeAllLessons(): Flow<List<ManifestationWithLessons>>

    @Transaction
    @Query("SELECT * FROM manifestation WHERE id = :manifestationId")
    fun observeAllLessonsByManifestationId(manifestationId: Int): Flow<List<ManifestationWithLessons>>

    @Transaction
    @Query("""
        SELECT 
            T1.id AS manifestation_id, T1.overview AS manifestation_overview, T1.description AS manifestation_description, T1.creation_date AS manifestation_creation_date, T1.due_date AS manifestation_due_date, T1.category AS manifestation_category,
            T2.id AS lesson_id, T2.day AS lesson_day, T2.subject AS lesson_subject, T2.manifestation_id AS lesson_manifestation_id
        FROM manifestation T1
        INNER JOIN lesson T2 
        ON T1.id = T2.manifestation_id
        WHERE T1.id = :manifestationId AND T2.day = :day
    """)
    fun observeManifestationAndLessonsForDay(manifestationId: Int, day: Int): Flow<ManifestationAndLesson?>

    @Transaction
    @Query("SELECT * FROM lesson WHERE day = :day")
    fun observeLessonsByDay(day: Int): Flow<List<LessonEntity>>

    @Query("SELECT COUNT(*) FROM lesson WHERE manifestation_id = :manifestationId")
    fun observeCountByManifestationId(manifestationId: Int): Flow<Int>

    @Query("SELECT * FROM lesson WHERE manifestation_id = :manifestationId ORDER BY id DESC LIMIT 1")
    fun observeLastLesson(manifestationId: Int): Flow<LessonEntity?>

}
