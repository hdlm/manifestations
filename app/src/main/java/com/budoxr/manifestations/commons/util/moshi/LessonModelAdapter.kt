package com.budoxr.manifestations.commons.util.moshi

import com.budoxr.manifestations.presentation.domain.LessonModel
import com.squareup.moshi.ToJson

import com.squareup.moshi.*

class LessonModelAdapter {
    @ToJson
    fun toJson(lessonModel: LessonModel): Map<String, Any> {
        return mapOf(
            "day" to lessonModel.day,
            "subject" to lessonModel.subject,
            "summary" to lessonModel.summary,
            "journal" to lessonModel.journal
        )
    }

    @FromJson
    fun fromJson(json: Map<String, Any>): LessonModel {
        return LessonModel(
            day = (json["day"] as Double).toInt(), // Moshi may parse JSON numbers as Double
            subject = json["subject"] as String,
            summary = (json["summary"] as List<String>),
            journal = (json["journal"] as List<String>),
            meditation = (json["meditation"] as String?)
        )
    }
}


