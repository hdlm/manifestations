package com.budoxr.manifestations.presentation.domain

data class TextContent(
    val paragraphs: List<String>,
    var text: String,
    var paragraphCount: Int,
)
