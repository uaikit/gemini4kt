package org.example

import kotlinx.serialization.Serializable

@Serializable
data class EmbedResponse(val embedding: Values)
