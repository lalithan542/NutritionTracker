package com.nutrition.tracker.data.repository

import com.nutrition.tracker.data.sampleFoods
import com.nutrition.tracker.domain.model.Food
import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepositoryImpl @Inject constructor() : NutritionRepository {

    private val entries = MutableStateFlow<List<FoodEntry>>(emptyList())

    override fun getEntriesForDate(date: LocalDate): Flow<List<FoodEntry>> =
        entries.map { it.filter { e -> e.date == date } }

    override suspend fun addEntry(entry: FoodEntry) {
        entries.update { it + entry }
    }

    override suspend fun removeEntry(entryId: String) {
        entries.update { it.filterNot { e -> e.id == entryId } }
    }

    override suspend fun searchFoods(query: String): List<Food> {
        if (query.isBlank()) return sampleFoods
        val lower = query.lowercase()
        return sampleFoods.filter {
            it.name.lowercase().contains(lower) || it.brand.lowercase().contains(lower)
        }
    }

    override suspend fun getFoodById(id: String): Food? = sampleFoods.find { it.id == id }
}
