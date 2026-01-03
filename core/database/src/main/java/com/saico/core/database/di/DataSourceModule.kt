package com.saico.core.database.di

import com.saico.core.database.datasource.local.GymExerciseLocalDataSource
import com.saico.core.database.datasource.local.GymExerciseLocalDataSourceImpl
import com.saico.core.database.datasource.local.UserProfileLocalDataSource
import com.saico.core.database.datasource.local.UserProfileLocalDataSourceImpl
import com.saico.core.database.datasource.local.WorkoutLocalDataSource
import com.saico.core.database.datasource.local.WorkoutLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    internal abstract fun bindWorkoutLocalDataSource(impl: WorkoutLocalDataSourceImpl): WorkoutLocalDataSource

    @Binds
    internal abstract fun bindUserProfileLocalDataSource(impl: UserProfileLocalDataSourceImpl): UserProfileLocalDataSource

    @Binds
    internal abstract fun bindGymExerciseLocalDataSource(impl: GymExerciseLocalDataSourceImpl): GymExerciseLocalDataSource
}