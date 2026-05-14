package com.nutrition.tracker.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.nutrition.tracker.BuildConfig
import com.nutrition.tracker.domain.model.ImageNutritionResult
import com.nutrition.tracker.domain.model.NutrientValues
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val TAG = "GeminiNutrition"
private const val GEMINI_MODEL = "gemini-3.1-flash-lite"
private const val GEMINI_BASE = "https://generativelanguage.googleapis.com/v1beta/models"

private val json = Json { ignoreUnknownKeys = true; isLenient = true }

class GeminiNutritionService @Inject constructor(private val httpClient: HttpClient) {

    suspend fun analyzeImage(bitmap: Bitmap, apiKey: String = BuildConfig.GEMINI_API_KEY): ImageNutritionResult {
        if (apiKey.isBlank()) {
            return error("GEMINI_API_KEY is not set in gradle.properties")
        }

        val endpoint = URLBuilder().apply {
            takeFrom("$GEMINI_BASE/$GEMINI_MODEL:generateContent")
            parameters.append("key", apiKey)
        }.buildString()

        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = NUTRITION_PROMPT),
                        GeminiPart(inlineData = InlineData("image/jpeg", bitmap.toBase64()))
                    )
                )
            )
        )

        return try {
            val httpResponse = httpClient.post {
                url(endpoint)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            val rawBody = httpResponse.bodyAsText()
            Log.d(TAG, "HTTP ${httpResponse.status.value}: $rawBody")

            if (!httpResponse.status.isSuccess()) {
                return error("API error ${httpResponse.status.value}: $rawBody")
            }

            val response = json.decodeFromString<GeminiResponse>(rawBody)
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (text.isNullOrBlank()) {
                // Surface the finish reason if available (e.g. SAFETY, RECITATION)
                val reason = response.candidates.firstOrNull()?.finishReason ?: "unknown"
                return error("Empty response from model (finishReason=$reason)")
            }

            parseNutritionJson(text)
        } catch (e: Exception) {
            Log.e(TAG, "Request failed", e)
            error("${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun parseNutritionJson(raw: String): ImageNutritionResult {
        // Strip markdown code fences Gemini adds despite being asked not to
        val cleaned = raw.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```")
            .trim()
            .let { s ->
                val start = s.indexOf('{')
                val end = s.lastIndexOf('}')
                if (start != -1 && end > start) s.substring(start, end + 1) else s
            }

        Log.d(TAG, "Parsing cleaned JSON: $cleaned")

        return try {
            val parsed = json.decodeFromString<NutritionJson>(cleaned)
            ImageNutritionResult(
                detectedFoodName = parsed.foodName,
                confidence = parsed.confidence,
                estimatedServing = parsed.servingSize,
                nutrients = NutrientValues(
                    calories = parsed.calories,
                    protein = parsed.protein,
                    carbohydrates = parsed.carbohydrates,
                    fat = parsed.fat,
                    fiber = parsed.fiber
                ),
                rawResponse = raw
            )
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse failed for: $cleaned", e)
            error("JSON parse failed: ${e.message}\n\nRaw: $raw")
        }
    }

    private fun error(message: String) = ImageNutritionResult(
        detectedFoodName = "Error",
        confidence = 0f,
        estimatedServing = "N/A",
        nutrients = NutrientValues.ZERO,
        rawResponse = message
    )

    private fun Bitmap.toBase64(): String {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}

private val NUTRITION_PROMPT = """
Analyze this food image. Respond with ONLY a raw JSON object, no markdown, no code fences, no explanation.
{"foodName":"string","servingSize":"string","calories":0,"protein":0,"carbohydrates":0,"fat":0,"fiber":0,"confidence":0.0}
Nutrient values in grams. Calories in kcal. confidence 0.0-1.0.
""".trimIndent()

@Serializable private data class GeminiRequest(val contents: List<GeminiContent>)
@Serializable private data class GeminiContent(val parts: List<GeminiPart>)
@Serializable private data class GeminiPart(
    val text: String? = null,
    val inlineData: InlineData? = null
)
@Serializable private data class InlineData(val mimeType: String, val data: String)
@Serializable private data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)
@Serializable private data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String = ""
)

@Serializable
private data class NutritionJson(
    val foodName: String = "",
    val servingSize: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbohydrates: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val confidence: Float = 0f
)
