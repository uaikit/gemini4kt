package org.example

import java.io.File
import java.util.Base64

fun main() {
    val apiKey = System.getenv("GEMINI_API_KEY")
    val gemini = Gemini(apiKey)
    val text = "Write a story about a magic backpack."
    val inputJson =
        GenerateContentRequest(
            listOf(Content(listOf(Part(text)))),
            safetySettings =
                listOf(
                    SafetySetting(
                        category = HarmCategory.HARM_CATEGORY_HARASSMENT,
                        threshold = Threshold.BLOCK_ONLY_HIGH,
                    ),
                ),
        )
    println(gemini.generateContent(inputJson).candidates[0].content.parts[0].text!!.replace("\n\n", "\n"))
    val inputJson2 = GenerateContentRequest(listOf(Content(listOf(Part(text)))))
    println(gemini.countTokens(inputJson2))
    val embedRequest =
        EmbedRequest(
            content = Content(listOf(Part(text))),
            model = "models/embedding-001",
        )
    println(gemini.embedContent(embedRequest))
    val batchEmbedRequest =
        BatchEmbedRequest(
            listOf(
                EmbedRequest(
                    content = Content(listOf(Part(text))),
                    model = "models/embedding-001",
                ),
            ),
        )
    println(gemini.batchEmbedContents(batchEmbedRequest))

    println(gemini.getModels())

    val path = Any::class.java.getResource("/scones.jpg")
    val imagePath = "scones.jpg"
    val imageFile = File(imagePath)
    val image = File(path.toURI())
    val base64Image = Base64.getEncoder().encodeToString(image.readBytes())

    val inputWithImage =
        GenerateContentRequest(
            listOf(
                Content(
                    listOf(
                        Part(text = "What is this picture?"),
                        Part(
                            inlineData =
                                InlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image,
                                ),
                        ),
                    ),
                ),
            ),
        )
    println(gemini.generateContent(inputWithImage, "gemini-pro-vision").candidates[0].content.parts[0].text!!.replace("\n\n", "\n"))
}

class ITTest
