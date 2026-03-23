package com.example.habittracker.data.di

import android.content.Context
import androidx.room.Room
import com.example.habittracker.PreferencesManager
import com.example.habittracker.data.local.HabitDao
import com.example.habittracker.data.local.HabitDatabase
import com.example.habittracker.data.local.HabitDateDao
import com.example.habittracker.data.repository.HabitDateRepositoryImpl
import com.example.habittracker.data.repository.HabitRepositoryImpl
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRepository
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
    fun provideHabitRepository(dao: HabitDao, prefs: PreferencesManager): HabitRepository{
        return HabitRepositoryImpl(prefs = prefs, dao = dao)
    }

    @Provides
    @Singleton
    fun provideHabitDateRepository(dateDao: HabitDateDao): HabitDateRepository{
        return HabitDateRepositoryImpl(dateDao)
    }
}