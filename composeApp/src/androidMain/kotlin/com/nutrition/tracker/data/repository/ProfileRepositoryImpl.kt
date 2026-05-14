package com.nutrition.tracker.data.repository

import com.nutrition.tracker.domain.model.UserProfile
import com.nutrition.tracker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {

    private val _profile = MutableStateFlow(UserProfile())

    override fun getProfile(): Flow<UserProfile> = _profile.asStateFlow()

    override suspend fun saveProfile(profile: UserProfile) {
        _profile.update { profile }
    }
}
