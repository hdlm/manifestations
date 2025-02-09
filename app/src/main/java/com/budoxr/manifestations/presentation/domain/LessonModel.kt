package com.budoxr.manifestations.presentation.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LessonModel(
    val id: Int? = null,
    @Json(name = "manifestation_id") var manifestationId: Int? = null,
    @Json(name = "day") val day: Int,
    @Json(name = "subject") val subject: String,
    val summary: List<String>,
    val journal: List<String>,
    val meditation: String?,
)

@JsonClass(generateAdapter = true)
data class LessonsWrapper(
    @Json(name = "lessons") val lessons: List<LessonModel>
)