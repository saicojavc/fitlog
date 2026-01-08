package com.saico.core.database.di

import com.saico.core.database.FitlogDatabase
import com.saico.core.database.dao.GymExerciseDao
import com.saico.core.database.dao.UserProfileDao
import com.saico.core.database.dao.WorkoutDao
import com.saico.core.database.dao.WorkoutSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun provideWorkoutDao(database: FitlogDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideWorkoutSessionDao(database: FitlogDatabase): WorkoutSessionDao = database.workoutSessionDao()

    @Provides
    fun provideUserProfileDao(database: FitlogDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideGymExerciseDao(database: FitlogDatabase): GymExerciseDao = database.gymExerciseDao()
}
