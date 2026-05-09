package com.example.data.data.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.room.Room
import com.example.data.BuildConfig
import com.example.data.data.local.database.HabitDao
import com.example.data.data.local.database.HabitDatabase
import com.example.data.data.local.database.HabitDateDao
import com.example.data.data.local.preferences.PreferencesManager
import com.example.data.data.remote.FacebookAuthDataSource
import com.example.data.data.remote.FirebaseAuthDataSource
import com.example.data.data.remote.FirebaseFacebookAuthDataSource
import com.example.data.data.remote.FirebaseGoogleAuthDataSource
import com.example.data.data.remote.FirebaseHabitDataSource
import com.example.data.data.remote.GoogleAuthDataSource
import com.example.data.data.repository.FacebookAuthRepositoryImpl
import com.example.data.data.repository.GoogleAuthRepositoryImpl
import com.example.data.data.repository.HabitDateRepositoryImpl
import com.example.data.data.repository.HabitRemoteRepositoryImpl
import com.example.data.data.repository.HabitRepositoryImpl
import com.example.data.data.repository.PreferencesRepositoryImpl
import com.example.data.data.repository.UserAuthRepositoryImpl
import com.example.domain.domain.repository.FacebookAuthRepository
import com.example.domain.domain.repository.GoogleAuthRepository
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.PreferencesRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideHabitDatabase(@ApplicationContext context: Context): HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            "habits.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao
    }

    @Provides
    @Singleton
    fun provideHabitDateDao(database: HabitDatabase): HabitDateDao {
        return database.dateDao
    }

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(prefs: PreferencesManager): PreferencesRepository {
        return PreferencesRepositoryImpl(prefs)
    }

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao): HabitRepository {
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
        firebaseHabitDataSource: FirebaseHabitDataSource,
    ): HabitRemoteRepository = HabitRemoteRepositoryImpl(firebaseHabitDataSource)

    @Provides
    @Singleton
    fun provideGoogleAuthRepository(googleAuthDataSource: GoogleAuthDataSource): GoogleAuthRepository = GoogleAuthRepositoryImpl(googleAuthDataSource)

    @Provides
    @Singleton
    fun provideFacebookAuthRepository(facebookAuthDataSource: FacebookAuthDataSource): FacebookAuthRepository =
        FacebookAuthRepositoryImpl(facebookAuthDataSource)
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
        dataBase: FirebaseDatabase,
    ): FirebaseHabitDataSource = FirebaseHabitDataSource(dataBase)

    @Provides
    @Singleton
    fun provideGoogleAuthDataSource(
        firebaseAuth: FirebaseAuth,
    ): GoogleAuthDataSource {
        return FirebaseGoogleAuthDataSource(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFacebookAuthDataSource(
        auth: FirebaseAuth
    ): FacebookAuthDataSource = FirebaseFacebookAuthDataSource(auth)
}