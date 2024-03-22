package org.example

import kotlinx.serialization.Serializable

@Serializable
data class EmbedContentRequest(
    val content: Content,
    val model: String,
    val taskType: TaskType? = null,
    val title: String? = null,
)
