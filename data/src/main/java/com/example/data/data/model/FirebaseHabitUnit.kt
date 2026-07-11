package com.example.data.data.model

data class FirebaseHabitUnit(
    val name: String = "",
    val remoteId: String = "",
    val streak: Int = 0,
    val creationDate: String = "",
    val checkedDates: List<String> = emptyList(),
    val hasReminder: Boolean = false,
    val reminderHour: Int = 0,
    val reminderMinute: Int = 0
)
