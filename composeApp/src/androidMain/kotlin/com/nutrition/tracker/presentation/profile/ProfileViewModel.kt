package com.nutrition.tracker.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrition.tracker.domain.model.ActivityLevel
import com.nutrition.tracker.domain.model.Gender
import com.nutrition.tracker.domain.model.UserProfile
import com.nutrition.tracker.domain.usecase.GetProfileUseCase
import com.nutrition.tracker.domain.usecase.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val profile: UserProfile = UserProfile(),
    val isSaved: Boolean = false,
    val isEditing: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val saveProfile: SaveProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getProfile().collect { profile ->
                _state.update { it.copy(profile = profile) }
            }
        }
    }

    fun startEditing() = _state.update { it.copy(isEditing = true, isSaved = false) }

    fun updateName(name: String) = _state.update { it.copy(profile = it.profile.copy(name = name)) }
    fun updateAge(age: Int) = _state.update { it.copy(profile = it.profile.copy(age = age)) }
    fun updateGender(gender: Gender) = _state.update { it.copy(profile = it.profile.copy(gender = gender)) }
    fun updateWeight(kg: Float) = _state.update { it.copy(profile = it.profile.copy(weightKg = kg)) }
    fun updateHeight(cm: Float) = _state.update { it.copy(profile = it.profile.copy(heightCm = cm)) }
    fun updateActivity(level: ActivityLevel) = _state.update { it.copy(profile = it.profile.copy(activityLevel = level)) }
    fun updateCalorieGoal(cal: Float) = _state.update { it.copy(profile = it.profile.copy(goalCalories = cal)) }
    fun updateProteinGoal(g: Float) = _state.update { it.copy(profile = it.profile.copy(goalProtein = g)) }
    fun updateCarbGoal(g: Float) = _state.update { it.copy(profile = it.profile.copy(goalCarbs = g)) }
    fun updateFatGoal(g: Float) = _state.update { it.copy(profile = it.profile.copy(goalFat = g)) }

    fun save() {
        viewModelScope.launch {
            saveProfile(_state.value.profile)
            _state.update { it.copy(isSaved = true, isEditing = false) }
        }
    }
}
