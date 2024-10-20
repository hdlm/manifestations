package com.budoxr.manifestations.commons.util.moshi

import com.budoxr.manifestations.presentation.domain.SessionModel
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class SessionModelAdapter {
    @ToJson
    fun toJson(sessionModel: SessionModel): Map<String, Any?> {
        return mapOf(
            "current_screen" to sessionModel.currentScreen,
            "lesson" to sessionModel.lesson
        )
    }

    @FromJson
    fun fromJson(json: Map<String, Any?>): SessionModel {
        return SessionModel(
            currentScreen = json["current_screen"].toString(),
            lesson = (json["lesson"] as Double).toInt() // Moshi may parse JSON numbers as Double
        )
    }
}
