package com.example.habittracker.presentation.state

import java.time.LocalDate
import java.time.Month

data class HabitDateState(
    val year: Int = LocalDate.now().year,
    val month: Month = LocalDate.now().month,
    val today: Int = LocalDate.now().dayOfMonth,
    val habitDates: List<LocalDate> = emptyList()
)