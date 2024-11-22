package com.budoxr.manifestations.presentation.domain


data class ConfigModel(
    val id: Int,
    val language: String,
    val country: String,
    var speechRate: Float,
)
