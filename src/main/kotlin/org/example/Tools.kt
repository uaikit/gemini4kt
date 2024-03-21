package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Tools(val functionDeclaration: List<FunctionDeclaration>)
