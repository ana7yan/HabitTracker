package com.example.habittracker.data.di

import android.content.Context
import androidx.room.Room
import com.example.habittracker.data.local.preferences.PreferencesManager
import com.example.habittracker.data.local.database.HabitDao
import com.example.habittracker.data.local.database.HabitDatabase
import com.example.habittracker.data.local.database.HabitDateDao
import com.example.habittracker.data.repository.HabitDateRepositoryImpl
import com.example.habittracker.data.repository.HabitRepositoryImpl
import com.example.habittracker.data.repository.PreferencesRepositoryImpl
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.PreferencesRepository
import com.example.habittracker.domain.usecase.AddHabitUseCase
import com.example.habittracker.domain.usecase.CheckAndResetHabitForNewDayUseCase
import com.example.habittracker.domain.usecase.CheckOutHabitUseCase
import com.example.habittracker.domain.usecase.DeleteHabitUseCase
import com.example.habittracker.domain.usecase.GetAllHabitsUseCase
import com.example.habittracker.domain.usecase.GetHabitDatesAsFlowUseCase
import com.example.habittracker.domain.usecase.GetHabitDatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun provideHabitDatabase(@ApplicationContext context: Context): HabitDatabase{
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            "habits.db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao{
        return database.habitDao
    }

    @Provides
    @Singleton
    fun provideHabitDateDao(database: HabitDatabase): HabitDateDao{
        return database.dateDao
    }

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext context: Context): PreferencesManager{
        return PreferencesManager(context)
    }
    @Provides
    @Singleton
    fun providePreferencesRepository(prefs: PreferencesManager): PreferencesRepository{
        return PreferencesRepositoryImpl(prefs)
    }

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao ): HabitRepository{
        return HabitRepositoryImpl(dao = dao)
    }

    @Provides
    @Singleton
    fun provideHabitDateRepository(dateDao: HabitDateDao): HabitDateRepository{
        return HabitDateRepositoryImpl(dateDao)
    }

    @Provides
    @Singleton
    fun provideDeleteHabitUseCase(
        repository: HabitRepository,
        dateRepository: HabitDateRepository
    ): DeleteHabitUseCase{
        return DeleteHabitUseCase(repository,dateRepository)
    }
    @Provides
    @Singleton
    fun provideAddHabitUseCase(
        repository: HabitRepository
    ): AddHabitUseCase{
        return AddHabitUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideCheckAndResetHabitForNewDayUseCase(
        repository: HabitRepository,
        preferencesRepository: PreferencesRepository
    ): CheckAndResetHabitForNewDayUseCase{
        return CheckAndResetHabitForNewDayUseCase(repository,preferencesRepository)
    }
    @Provides
    @Singleton
    fun provideCheckOutHabitUseCase(
        repository: HabitRepository,
        dateRepository: HabitDateRepository
    ): CheckOutHabitUseCase{
        return CheckOutHabitUseCase(repository,dateRepository)
    }
    @Provides
    @Singleton
    fun provideGetAllHabitsUseCase(
        repository: HabitRepository
    ): GetAllHabitsUseCase{
        return GetAllHabitsUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideGetHabitDatesAsFlowUseCase(
        repository: HabitDateRepository
    ): GetHabitDatesAsFlowUseCase{
        return GetHabitDatesAsFlowUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideGetHabitDatesUseCase(
        repository: HabitDateRepository
    ): GetHabitDatesUseCase {
        return GetHabitDatesUseCase(repository)
    }
}