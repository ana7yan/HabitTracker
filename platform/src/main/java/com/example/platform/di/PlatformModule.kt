package com.example.platform.di

import android.content.Context
import androidx.work.WorkManager
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.sceduler.NotificationPermissionChecker
import com.example.domain.domain.sceduler.ReminderScheduler
import com.example.platform.notifications.NotificationPermissionCheckerImpl
import com.example.platform.scheduler.WorkManagerReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlatformModule {
    @Provides
    @Singleton
    fun provideWorkManagerReminderScheduler(
        workManager: WorkManager,
        notificationPermissionChecker: NotificationPermissionChecker
    ): ReminderScheduler = WorkManagerReminderScheduler(workManager, notificationPermissionChecker)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideNotificationPermissionChecker(
        @ApplicationContext context: Context
    ): NotificationPermissionChecker =
        NotificationPermissionCheckerImpl(context)
}
