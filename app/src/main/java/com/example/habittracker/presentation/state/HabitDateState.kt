package com.example.habittracker.presentation.state

import com.example.domain.domain.model.Habit
import java.time.LocalDate
import java.time.Month

data class HabitDateState(
    val year: Int = LocalDate.now().year,
    val month: Month = LocalDate.now().month,
    val today: Int = LocalDate.now().dayOfMonth,
    val habitDates: List<LocalDate> = emptyList(),
    val isTimePickerVisible: Boolean = false,
    val hasReminder: Boolean = false,
    val reminderHour: Int = 0,
    val reminderMinute: Int = 0
)