package com.budoxr.manifestations.presentation.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionModel(
    @Json(name = "current_screen") var currentScreen: String? = null,
    var manifestation: Int = 0,
    var lesson: Int = 0
)
