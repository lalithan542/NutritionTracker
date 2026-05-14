package com.nutrition.tracker.domain.repository

import com.nutrition.tracker.domain.model.Food
import com.nutrition.tracker.domain.model.FoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface NutritionRepository {
    fun getEntriesForDate(date: LocalDate): Flow<List<FoodEntry>>
    suspend fun addEntry(entry: FoodEntry)
    suspend fun removeEntry(entryId: String)
    suspend fun searchFoods(query: String): List<Food>
    suspend fun getFoodById(id: String): Food?
}
