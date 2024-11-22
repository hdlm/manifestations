package com.budoxr.manifestations.presentation.domain

import java.util.Date

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ManifestationModel(
    val id: Long?,
    val overview: String,
    val description: String,
    @Json(name = "creation_date") val creationDate: String,
    @Json(name = "due_date") val dueDate: String,
    val category: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ManifestationModel) return false

        return id == other.id && overview == other.overview && description == other.description &&
                creationDate == other.creationDate && dueDate == other.dueDate && category == other.category
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + overview.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + dueDate.hashCode()
        result = 31 * result + category.hashCode()
        return result

    }
}

@JsonClass(generateAdapter = true)
data class ManifestationsWrapper(
    @Json(name = "manifestations") val manifestations: List<ManifestationModel>
)


