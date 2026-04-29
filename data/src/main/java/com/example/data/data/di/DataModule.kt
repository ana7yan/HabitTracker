package com.example.data.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.data.local.preferences.PreferencesManager
import com.example.data.data.local.database.HabitDao
import com.example.data.data.local.database.HabitDatabase
import com.example.data.data.local.database.HabitDateDao
import com.example.data.data.remote.FirebaseAuthDataSource
import com.example.data.data.remote.FirebaseHabitDataSource
import com.example.data.data.repository.HabitDateRepositoryImpl
import com.example.data.data.repository.HabitRemoteRepositoryImpl
import com.example.data.data.repository.HabitRepositoryImpl
import com.example.data.data.repository.PreferencesRepositoryImpl
import com.example.data.data.repository.UserAuthRepositoryImpl
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.PreferencesRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule{
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
    fun providePreferencesRepository(prefs: PreferencesManager): PreferencesRepository {
        return PreferencesRepositoryImpl(prefs)
    }

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao ): HabitRepository {
        return HabitRepositoryImpl(dao = dao)
    }

    @Provides
    @Singleton
    fun provideHabitDateRepository(dateDao: HabitDateDao): HabitDateRepository {
        return HabitDateRepositoryImpl(dateDao)
    }
    @Provides
    @Singleton
    fun provideUserAuthRepository(firebaseAuth: FirebaseAuthDataSource): UserAuthRepository {
        return UserAuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideHabitRemoteRepository(
        firebaseHabitDataSource: FirebaseHabitDataSource
    ): HabitRemoteRepository = HabitRemoteRepositoryImpl(firebaseHabitDataSource)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRemoteDateSource(auth: FirebaseAuth): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseHabitDataSource(
        dataBase: FirebaseDatabase
    ): FirebaseHabitDataSource = FirebaseHabitDataSource(dataBase)

}