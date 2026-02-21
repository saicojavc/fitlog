package com.saico.core.network.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.network.repository.AuthRepositoryImpl
import com.saico.core.network.repository.SyncRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase = 
            FirebaseDatabase.getInstance("https://fitlog-cb7c8-default-rtdb.firebaseio.com/")
    }
}
