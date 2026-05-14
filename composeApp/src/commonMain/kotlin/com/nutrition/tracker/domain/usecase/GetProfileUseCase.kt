package com.nutrition.tracker.domain.usecase

import com.nutrition.tracker.domain.model.UserProfile
import com.nutrition.tracker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<UserProfile> = repository.getProfile()
}
