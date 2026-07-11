package com.example.domain.domain.sceduler

import com.example.domain.domain.model.Habit

interface ReminderScheduler {
    fun scheduleReminder(
        habit: Habit
    )

    fun cancelReminder(reminderId: Long)
}