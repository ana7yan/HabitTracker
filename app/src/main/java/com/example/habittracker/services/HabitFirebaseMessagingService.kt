package com.example.habittracker.services

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.platform.notifications.NotificationHelper
import com.example.platform.worker.HabitReminderWorker.Companion.KEY_REMINDER_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HabitFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val habitId =
            message.data["habitId"]

        Log.d(
            "FCM",
            "HabitId = $habitId"
        )
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationId = message.notification?.notificationCount ?: 0
        NotificationHelper.showNotification(
            context = this,
            notificationId = notificationId,
            title = message.notification?.title ?: "No Title",
            body = message.notification?.body ?: "No Body",
            icon = R.drawable.ic_launcher_foreground,
            pendingIntent = pendingIntent,
        )
        Log.d("FCM", "Title: ${message.notification?.title}")
        Log.d("FCM", "Body: ${message.notification?.body}")
        Log.d("FCM", "Data: ${message.data}")
    }
}