package com.example.habittracker

import java.time.LocalDate

sealed interface HabitDateEvent {
    data class NextMonth(val habitId: Int): HabitDateEvent
    data class PreviousMonth(val habitId: Int): HabitDateEvent
    data class ThisMonth(val habitId: Int): HabitDateEvent
}