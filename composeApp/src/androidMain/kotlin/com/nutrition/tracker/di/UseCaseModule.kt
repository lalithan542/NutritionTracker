package com.nutrition.tracker.di

import com.nutrition.tracker.domain.repository.NutritionRepository
import com.nutrition.tracker.domain.repository.ProfileRepository
import com.nutrition.tracker.domain.usecase.AddFoodEntryUseCase
import com.nutrition.tracker.domain.usecase.GetDailyEntriesUseCase
import com.nutrition.tracker.domain.usecase.GetProfileUseCase
import com.nutrition.tracker.domain.usecase.RemoveFoodEntryUseCase
import com.nutrition.tracker.domain.usecase.SaveProfileUseCase
import com.nutrition.tracker.domain.usecase.SearchFoodsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides fun provideGetDailyEntries(r: NutritionRepository) = GetDailyEntriesUseCase(r)
    @Provides fun provideAddFoodEntry(r: NutritionRepository) = AddFoodEntryUseCase(r)
    @Provides fun provideRemoveFoodEntry(r: NutritionRepository) = RemoveFoodEntryUseCase(r)
    @Provides fun provideSearchFoods(r: NutritionRepository) = SearchFoodsUseCase(r)
    @Provides fun provideGetProfile(r: ProfileRepository) = GetProfileUseCase(r)
    @Provides fun provideSaveProfile(r: ProfileRepository) = SaveProfileUseCase(r)
}
