package com.nutrition.tracker.domain.usecase

import com.nutrition.tracker.domain.model.Food
import com.nutrition.tracker.domain.repository.NutritionRepository

class SearchFoodsUseCase(private val repository: NutritionRepository) {
    suspend operator fun invoke(query: String): List<Food> = repository.searchFoods(query)
}
