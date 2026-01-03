package com.saico.core.domain.di

import com.saico.core.domain.data.repository.GymExerciseRepositoryImpl
import com.saico.core.domain.data.repository.UserProfileRepositoryImpl
import com.saico.core.domain.data.repository.WorkoutRepositoryImpl
import com.saico.core.domain.repository.GymExerciseRepository
import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    internal abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    internal abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    internal abstract fun bindGymExerciseRepository(impl: GymExerciseRepositoryImpl): GymExerciseRepository
}
