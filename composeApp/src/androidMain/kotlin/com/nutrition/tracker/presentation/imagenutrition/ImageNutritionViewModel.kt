package com.nutrition.tracker.presentation.imagenutrition

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrition.tracker.data.remote.GeminiNutritionService
import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.model.Food
import com.nutrition.tracker.domain.model.ImageNutritionResult
import com.nutrition.tracker.domain.model.MealType
import com.nutrition.tracker.domain.usecase.AddFoodEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID
import javax.inject.Inject

data class ImageNutritionState(
    val capturedBitmap: Bitmap? = null,
    val result: ImageNutritionResult? = null,
    val isAnalyzing: Boolean = false,
    val isLogged: Boolean = false,
    val selectedMeal: MealType = MealType.LUNCH,
    val error: String? = null
)

@HiltViewModel
class ImageNutritionViewModel @Inject constructor(
    private val geminiService: GeminiNutritionService,
    private val addFoodEntry: AddFoodEntryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ImageNutritionState())
    val state: StateFlow<ImageNutritionState> = _state.asStateFlow()

    fun onImageCaptured(bitmap: Bitmap) {
        _state.update { it.copy(capturedBitmap = bitmap, result = null, isLogged = false, error = null) }
        analyzeImage(bitmap)
    }

    private fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true) }
            val result = geminiService.analyzeImage(bitmap)
            _state.update { it.copy(result = result, isAnalyzing = false) }
        }
    }

    fun onMealChange(meal: MealType) = _state.update { it.copy(selectedMeal = meal) }

    fun logDetectedFood() {
        val result = _state.value.result ?: return
        viewModelScope.launch {
            val food = Food(
                id = UUID.randomUUID().toString(),
                name = result.detectedFoodName,
                brand = "AI Detected",
                servingSize = 100f,
                servingUnit = "g",
                calories = result.nutrients.calories,
                protein = result.nutrients.protein,
                carbohydrates = result.nutrients.carbohydrates,
                fat = result.nutrients.fat,
                fiber = result.nutrients.fiber
            )
            addFoodEntry(
                FoodEntry(
                    id = UUID.randomUUID().toString(),
                    food = food,
                    amount = 100f,
                    mealType = _state.value.selectedMeal,
                    date = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )
            )
            _state.update { it.copy(isLogged = true) }
        }
    }

    fun reset() = _state.update { ImageNutritionState() }
}
