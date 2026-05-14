package com.nutrition.tracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrition.tracker.domain.model.FoodEntry
import com.nutrition.tracker.domain.model.MealType
import com.nutrition.tracker.domain.model.NutrientValues
import com.nutrition.tracker.domain.model.UserProfile
import com.nutrition.tracker.domain.usecase.GetDailyEntriesUseCase
import com.nutrition.tracker.domain.usecase.GetProfileUseCase
import com.nutrition.tracker.domain.usecase.RemoveFoodEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

data class DashboardState(
    val entries: List<FoodEntry> = emptyList(),
    val profile: UserProfile = UserProfile(),
    val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
) {
    val totalNutrients: NutrientValues
        get() = entries.fold(NutrientValues.ZERO) { acc, e -> acc + e.nutrients }

    val entriesByMeal: Map<MealType, List<FoodEntry>>
        get() = MealType.entries.associateWith { meal -> entries.filter { it.mealType == meal } }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDailyEntries: GetDailyEntriesUseCase,
    private val getProfile: GetProfileUseCase,
    private val removeFoodEntry: RemoveFoodEntryUseCase
) : ViewModel() {

    private val selectedDate = MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    // Re-subscribes to entries whenever date changes
    private val entriesFlow = selectedDate.flatMapLatest { date -> getDailyEntries(date) }

    val state: StateFlow<DashboardState> = combine(
        selectedDate,
        entriesFlow,
        getProfile()
    ) { date, entries, profile ->
        DashboardState(entries = entries, profile = profile, selectedDate = date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())

    fun removeEntry(entryId: String) {
        viewModelScope.launch { removeFoodEntry(entryId) }
    }

    fun changeDate(date: LocalDate) {
        selectedDate.value = date
    }
}
