package com.example.domain.domain.model

data class Habit(
    val id: Int = 0,
    val remoteId: String? = null,
    val name: String = "",
    val streak: Int = 0,
    val lastCompletedDate: String = "",
    val isCompletedToday: Boolean = false,
    val creationDate: String = "",
    val checkedDates: List<String> = emptyList(),
    val hasReminder: Boolean = false,
    val reminderHour: Int = 0,
    val reminderMinute: Int = 0
)
