package org.example

import kotlinx.serialization.Serializable

@Serializable
data class BatchEmbedResponse(val embeddings: List<Values>)
