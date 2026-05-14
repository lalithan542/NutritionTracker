package com.nutrition.tracker.domain.usecase

import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class GetDailyEntriesUseCase(private val repository: NutritionRepository) {
    operator fun invoke(date: LocalDate): Flow<List<FoodEntry>> =
        repository.getEntriesForDate(date)
}
