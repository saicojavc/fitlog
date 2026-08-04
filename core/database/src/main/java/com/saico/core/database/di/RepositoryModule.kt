package com.saico.core.database.di

import com.saico.core.database.repository.GymExerciseRepositoryImpl
import com.saico.core.database.repository.OutdoorSessionRepositoryImpl
import com.saico.core.database.repository.UserProfileRepositoryImpl
import com.saico.core.database.repository.WgerRepositoryImpl
import com.saico.core.database.repository.WorkoutRepositoryImpl
import com.saico.core.database.repository.WorkoutSessionRepositoryImpl
import com.saico.core.datastore.repository.OnboardingRepositoryImpl
import com.saico.core.domain.repository.GymExerciseRepository
import com.saico.core.domain.repository.OnboardingRepository
import com.saico.core.domain.repository.OutdoorSessionRepository
import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.domain.repository.WgerRepository
import com.saico.core.domain.repository.WorkoutRepository
import com.saico.core.domain.repository.WorkoutSessionRepository
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
    internal abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    internal abstract fun bindWorkoutSessionRepository(impl: WorkoutSessionRepositoryImpl): WorkoutSessionRepository

    @Binds
    @Singleton
    internal abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    internal abstract fun bindGymExerciseRepository(impl: GymExerciseRepositoryImpl): GymExerciseRepository

    @Binds
    @Singleton
    internal abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    internal abstract fun bindOutdoorSessionRepository(impl: OutdoorSessionRepositoryImpl): OutdoorSessionRepository

    @Binds
    @Singleton
    internal abstract fun bindWgerRepository(impl: WgerRepositoryImpl): WgerRepository
}
