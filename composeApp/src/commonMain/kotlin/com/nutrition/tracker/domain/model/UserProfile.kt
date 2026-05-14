package com.nutrition.tracker.domain.model

import kotlinx.serialization.Serializable

enum class Gender { MALE, FEMALE, OTHER }

enum class ActivityLevel(val label: String, val multiplier: Float) {
    SEDENTARY("Sedentary", 1.2f),
    LIGHTLY_ACTIVE("Lightly Active", 1.375f),
    MODERATELY_ACTIVE("Moderately Active", 1.55f),
    VERY_ACTIVE("Very Active", 1.725f),
    EXTRA_ACTIVE("Extra Active", 1.9f)
}

@Serializable
data class UserProfile(
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val weightKg: Float = 70f,
    val heightCm: Float = 170f,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val goalCalories: Float = 0f,
    val goalProtein: Float = 0f,
    val goalCarbs: Float = 0f,
    val goalFat: Float = 0f
) {
    val bmi: Float get() {
        val hm = heightCm / 100f
        return weightKg / (hm * hm)
    }

    val bmiCategory: String get() = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25f -> "Normal"
        bmi < 30f -> "Overweight"
        else -> "Obese"
    }

    // Mifflin-St Jeor BMR × activity multiplier = TDEE
    val tdee: Float get() {
        val bmr = when (gender) {
            Gender.MALE -> 10f * weightKg + 6.25f * heightCm - 5f * age + 5f
            Gender.FEMALE -> 10f * weightKg + 6.25f * heightCm - 5f * age - 161f
            Gender.OTHER -> 10f * weightKg + 6.25f * heightCm - 5f * age - 78f
        }
        return bmr * activityLevel.multiplier
    }

    val effectiveCalorieGoal: Float get() = if (goalCalories > 0) goalCalories else tdee
    val effectiveProteinGoal: Float get() = if (goalProtein > 0) goalProtein else weightKg * 1.6f
    val effectiveCarbGoal: Float get() = if (goalCarbs > 0) goalCarbs else effectiveCalorieGoal * 0.45f / 4f
    val effectiveFatGoal: Float get() = if (goalFat > 0) goalFat else effectiveCalorieGoal * 0.30f / 9f
}
