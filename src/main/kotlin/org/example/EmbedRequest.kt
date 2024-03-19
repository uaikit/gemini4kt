package org.example

import kotlinx.serialization.Serializable

@Serializable
data class EmbedRequest(val content: Content, val model: String)
