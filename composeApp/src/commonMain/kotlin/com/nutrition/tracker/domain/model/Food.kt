package com.nutrition.tracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Food(
    val id: String,
    val name: String,
    val brand: String = "",
    val servingSize: Float,
    val servingUnit: String = "g",
    val calories: Float,
    val protein: Float,
    val carbohydrates: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f
) {
    fun scaledTo(amount: Float): NutrientValues {
        val factor = amount / servingSize
        return NutrientValues(
            calories = calories * factor,
            protein = protein * factor,
            carbohydrates = carbohydrates * factor,
            fat = fat * factor,
            fiber = fiber * factor,
            sugar = sugar * factor,
            sodium = sodium * factor
        )
    }
}

@Serializable
data class NutrientValues(
    val calories: Float,
    val protein: Float,
    val carbohydrates: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f
) {
    operator fun plus(other: NutrientValues) = NutrientValues(
        calories = calories + other.calories,
        protein = protein + other.protein,
        carbohydrates = carbohydrates + other.carbohydrates,
        fat = fat + other.fat,
        fiber = fiber + other.fiber,
        sugar = sugar + other.sugar,
        sodium = sodium + other.sodium
    )

    companion object {
        val ZERO = NutrientValues(0f, 0f, 0f, 0f)
    }
}
