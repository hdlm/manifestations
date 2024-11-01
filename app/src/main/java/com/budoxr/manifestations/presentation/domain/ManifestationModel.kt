package com.budoxr.manifestations.presentation.domain

import java.util.Date

data class ManifestationModel(
    val id: Long?,
    val overview: String,
    val description: String,
    val creationDate: Date,
    val dueDate: Date,
    val category: String,
)
