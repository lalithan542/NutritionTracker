package com.nutrition.tracker.di

import com.nutrition.tracker.data.repository.NutritionRepositoryImpl
import com.nutrition.tracker.data.repository.ProfileRepositoryImpl
import com.nutrition.tracker.domain.repository.NutritionRepository
import com.nutrition.tracker.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(impl: NutritionRepositoryImpl): NutritionRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
