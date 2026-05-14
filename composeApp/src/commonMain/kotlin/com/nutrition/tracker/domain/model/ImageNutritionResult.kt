package com.nutrition.tracker.domain.model

data class ImageNutritionResult(
    val detectedFoodName: String,
    val confidence: Float,
    val estimatedServing: String,
    val nutrients: NutrientValues,
    val rawResponse: String = ""
)
