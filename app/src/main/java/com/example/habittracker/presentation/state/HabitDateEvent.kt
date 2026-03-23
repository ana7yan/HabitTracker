package com.example.habittracker.presentation.state

sealed interface HabitDateEvent {
    data class NextMonth(val habitId: Int): HabitDateEvent
    data class PreviousMonth(val habitId: Int): HabitDateEvent
    data class ThisMonth(val habitId: Int): HabitDateEvent
}