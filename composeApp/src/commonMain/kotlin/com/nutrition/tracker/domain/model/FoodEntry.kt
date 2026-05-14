package com.nutrition.tracker.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class FoodEntry(
    val id: String,
    val food: Food,
    val amount: Float,
    val mealType: MealType,
    val date: LocalDate
) {
    val nutrients: NutrientValues get() = food.scaledTo(amount)
}
