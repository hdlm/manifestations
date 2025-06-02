package com.budoxr.manifestations.data.mapper

import com.budoxr.manifestations.data.database.entities.ConfigEntity
import com.budoxr.manifestations.presentation.domain.ConfigModel

fun ConfigModel.toEntity() =
    ConfigEntity(
        id = id,
        language = language,
        country = country,
        speechRate = speechRate
    )

fun ConfigEntity.toModel() =
    ConfigModel(
        id = id,
        language = language,
        country = country,
        speechRate = speechRate
    )

fun defaultConfigModel() =
    ConfigModel(
        id = 1,
        language = "es",
        country = "MX",
        speechRate = 1.0f
    )

