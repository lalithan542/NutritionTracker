package com.nutrition.tracker.domain.usecase

import com.nutrition.tracker.domain.repository.NutritionRepository

class RemoveFoodEntryUseCase(private val repository: NutritionRepository) {
    suspend operator fun invoke(entryId: String) = repository.removeEntry(entryId)
}
