package com.example.platform.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.sceduler.NotificationPermissionChecker
import com.example.platform.R
import com.example.platform.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class HabitReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: HabitRepository,
    private val notificationPermissionChecker: NotificationPermissionChecker
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val notificationId = inputData.getLong(KEY_REMINDER_ID, 0L).toInt()
        val habit = repository.getHabitById(notificationId)
        if (habit.isCompletedToday || !notificationPermissionChecker.areNotificationsEnabled()) {
            return Result.success()
        }
        val title = "Time to ${habit.name}"
        val body = "Current streak is ${habit.streak}"
        val intent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        NotificationHelper.showNotification(
            context = applicationContext,
            notificationId = notificationId,
            title = title,
            body = body,
            pendingIntent = pendingIntent,
            icon = R.drawable.baseline_notifications_24
        )
        return Result.success()
    }
}