package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String,
    val index: Int,
    val safetyRatings: List<SafetyRating>,
)
