package com.example.habittracker.presentation.event

sealed interface HabitDateEvent {
    data class LoadHabit(val habitId: Int): HabitDateEvent
    data class NextMonth(val habitId: Int): HabitDateEvent
    data class PreviousMonth(val habitId: Int): HabitDateEvent
    data class ThisMonth(val habitId: Int): HabitDateEvent
    object OpenTimePicker: HabitDateEvent
    object CloseTimePicker: HabitDateEvent
    data class DeleteReminder(val habitId: Int): HabitDateEvent
    data class SaveReminder(val reminderHour: Int, val reminderMinute: Int,val habitId: Int): HabitDateEvent
}