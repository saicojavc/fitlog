package com.saico.core.database.di

import android.content.Context
import androidx.room.Room
import com.saico.core.database.DB_NAME
import com.saico.core.database.FitlogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFitlogDatabase(@ApplicationContext context: Context): FitlogDatabase {
        return Room.databaseBuilder(
            context,
            FitlogDatabase::class.java,
            DB_NAME
        )
        .addMigrations(FitlogDatabase.MIGRATION) // Añadimos la migración segura
        .fallbackToDestructiveMigration() // Solo como último recurso si falla la migración
        .build()
    }
}
