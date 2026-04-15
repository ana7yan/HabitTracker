package com.example.habittracker.data.di

import android.content.Context
import androidx.compose.ui.layout.FirstBaseline
import androidx.room.Room
import com.example.habittracker.data.local.preferences.PreferencesManager
import com.example.habittracker.data.local.database.HabitDao
import com.example.habittracker.data.local.database.HabitDatabase
import com.example.habittracker.data.local.database.HabitDateDao
import com.example.habittracker.data.remote.FirebaseAuthDataSource
import com.example.habittracker.data.remote.FirebaseHabitDataSource
import com.example.habittracker.data.repository.HabitDateRepositoryImpl
import com.example.habittracker.data.repository.HabitRemoteRepositoryImpl
import com.example.habittracker.data.repository.HabitRepositoryImpl
import com.example.habittracker.data.repository.PreferencesRepositoryImpl
import com.example.habittracker.data.repository.UserAuthRepositoryImpl
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.PreferencesRepository
import com.example.habittracker.domain.repository.UserAuthRepository
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
    fun provideUserAuthRepository(firebaseAuth: FirebaseAuthDataSource): UserAuthRepository{
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