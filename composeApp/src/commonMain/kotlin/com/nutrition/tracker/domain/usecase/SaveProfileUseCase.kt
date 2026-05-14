package com.nutrition.tracker.domain.usecase

import com.nutrition.tracker.domain.model.UserProfile
import com.nutrition.tracker.domain.repository.ProfileRepository

class SaveProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: UserProfile) = repository.saveProfile(profile)
}
