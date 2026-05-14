package com.nutrition.tracker.presentation.logfood

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrition.tracker.domain.model.Food
import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.model.MealType
import com.nutrition.tracker.domain.usecase.AddFoodEntryUseCase
import com.nutrition.tracker.domain.usecase.SearchFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID
import javax.inject.Inject

data class LogFoodState(
    val food: Food? = null,
    val amount: String = "",
    val selectedMeal: MealType = MealType.BREAKFAST,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LogFoodViewModel @Inject constructor(
    private val addFoodEntry: AddFoodEntryUseCase,
    private val searchFoods: SearchFoodsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val foodId: String? = savedStateHandle["foodId"]

    private val _state = MutableStateFlow(LogFoodState())
    val state: StateFlow<LogFoodState> = _state.asStateFlow()

    init {
        foodId?.let { loadFood(it) }
    }

    private fun loadFood(id: String) {
        viewModelScope.launch {
            val results = searchFoods("")
            val food = results.find { it.id == id }
            food?.let {
                _state.update { s -> s.copy(food = food, amount = food.servingSize.toString()) }
            }
        }
    }

    fun onAmountChange(value: String) = _state.update { it.copy(amount = value) }
    fun onMealChange(meal: MealType) = _state.update { it.copy(selectedMeal = meal) }

    fun logFood(date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())) {
        val food = _state.value.food ?: return
        val amount = _state.value.amount.toFloatOrNull()
        if (amount == null || amount <= 0f) {
            _state.update { it.copy(error = "Enter a valid amount") }
            return
        }
        viewModelScope.launch {
            addFoodEntry(
                FoodEntry(
                    id = UUID.randomUUID().toString(),
                    food = food,
                    amount = amount,
                    mealType = _state.value.selectedMeal,
                    date = date
                )
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
