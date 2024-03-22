package org.example

import kotlinx.serialization.Serializable

@Serializable
data class CountTokensRequest(
    val contents: List<Content>,
)
