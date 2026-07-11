package com.example.domain.domain.sceduler

interface NotificationPermissionChecker {
    fun areNotificationsEnabled(): Boolean
}