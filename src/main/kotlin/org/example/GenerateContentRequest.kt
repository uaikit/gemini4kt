package org.example

import kotlinx.serialization.Serializable

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting> = emptyList(),
    val generationConfig: GenerationConfig? = null,
)
