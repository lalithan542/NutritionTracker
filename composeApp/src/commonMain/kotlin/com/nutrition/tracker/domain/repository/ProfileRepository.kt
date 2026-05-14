package com.nutrition.tracker.domain.repository

import com.nutrition.tracker.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<UserProfile>
    suspend fun saveProfile(profile: UserProfile)
}
