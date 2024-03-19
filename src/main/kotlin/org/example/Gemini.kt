package org.example

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * A logger for logging messages. Uses KotlinLogging library for simplified logging.
 */
private val logger = KotlinLogging.logger {}

/**
 * Represents a client for interacting with the Gemini API, providing methods to extract content,
 * embed content, and retrieve model information.
 *
 * @property apiKey The API key used for authenticating requests to the Gemini API.
 */
class Gemini(private val apiKey: String) {
    /**
     * JSON configuration setup to ignore unknown keys during deserialization.
     */
    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"

    /**
     * Extracts content based on the provided input JSON using the Gemini Pro model.
     *
     * @param inputJson The request payload for content extraction.
     * @return The response from the Gemini API as a [Response] object.
     */
    fun extract(inputJson: ContentRequest): Response {
        val urlString = "$baseUrl/gemini-pro:generateContent?key=$apiKey"
        return json.decodeFromString<Response>(getContent(urlString, json.encodeToString<ContentRequest>(inputJson)))
    }

    /**
     * Extracts content based on the provided input JSON using the Gemini Pro Vision model.
     *
     * @param inputJson The request payload for content extraction.
     * @return The response from the Gemini API as a [Response] object.
     */
    fun extract2(inputJson: ContentRequest): Response {
        val urlString = "$baseUrl/gemini-pro-vision:generateContent?key=$apiKey"
        return json.decodeFromString<Response>(getContent(urlString, json.encodeToString<ContentRequest>(inputJson)))
    }

    fun countTokens(inputJson: ContentRequest): TotalTokens {
        val urlString = "$baseUrl/gemini-pro:countTokens?key=$apiKey"
        println(inputJson)
        return json.decodeFromString<TotalTokens>(getContent(urlString, json.encodeToString<ContentRequest>(inputJson)))
    }

    /**
     * Embeds contents in batch using the embedding-001 model.
     *
     * @param inputJson The batch embed request payload.
     * @return The batch embed response as a [BatchEmbedResponse] object.
     */
    fun batchEmbedContents(inputJson: BatchEmbedRequest): BatchEmbedResponse {
        val urlString = "$baseUrl/embedding-001:batchEmbedContents?key=$apiKey"
        return json.decodeFromString<BatchEmbedResponse>(getContent(urlString, json.encodeToString<BatchEmbedRequest>(inputJson)))
    }

    /**
     * Embeds content using the embedding-001 model.
     *
     * @param inputJson The embed request payload.
     * @return The embed response as an [EmbedResponse] object.
     */
    fun embedContent(inputJson: EmbedRequest): EmbedResponse {
        val urlString = "$baseUrl/embedding-001:embedContent?key=$apiKey"
        return json.decodeFromString<EmbedResponse>(getContent(urlString, json.encodeToString<EmbedRequest>(inputJson)))
    }

    /**
     * Retrieves a collection of models available in the Gemini API.
     *
     * @return The collection of models as a [ModelCollection] object.
     */
    fun getModels(): ModelCollection {
        val urlString = "$baseUrl?key=$apiKey"
        return json.decodeFromString<ModelCollection>(getContent(urlString))
    }

    /**
     * Performs a POST request to the specified URL string with the given input JSON payload.
     *
     * @param urlStr The URL to which the POST request is made.
     * @param inputJson The JSON payload for the request.
     * @return The response body as a String.
     */
    fun getContent(
        urlStr: String,
        inputJson: String? = null,
    ): String {
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = if (inputJson == null) "GET" else "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            if (inputJson != null) {
                conn.doOutput = true
                OutputStreamWriter(conn.outputStream).use { writer -> writer.write(inputJson) }
            }

            logger.info { "Response Code: ${conn.responseCode}" }
            conn.inputStream.bufferedReader().use { reader ->
                return reader.readText()
            }
        } catch (e: IOException) {
            logger.error { e.stackTrace.contentToString() }
            return ""
        }
    }
}
