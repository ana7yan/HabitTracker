package com.example.platform.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.example.domain.domain.sceduler.NotificationPermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationPermissionCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationPermissionChecker{
    override fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context)
            .areNotificationsEnabled()
    }
}