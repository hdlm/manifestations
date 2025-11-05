package com.budoxr.manifestations.presentation.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ManifestationModel(
    val id: Int?,
    val overview: String,
    val description: String,
    @field:Json(name = "creation_date") val creationDate: String,
    @field:Json(name = "due_date") val dueDate: String,
    val category: String,
)

@JsonClass(generateAdapter = true)
data class ManifestationsWrapper(
    @field:Json(name = "manifestations") val manifestations: List<ManifestationModel>
)