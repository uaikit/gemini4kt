package org.example

import kotlinx.serialization.Serializable

@Serializable
data class ContentRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting> = emptyList(),
    val generationConfig: GenerationConfig? = null,
)
