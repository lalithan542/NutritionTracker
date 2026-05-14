package com.nutrition.tracker.domain.usecase

import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.repository.NutritionRepository

class AddFoodEntryUseCase(private val repository: NutritionRepository) {
    suspend operator fun invoke(entry: FoodEntry) = repository.addEntry(entry)
}
