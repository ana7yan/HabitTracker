package com.example.habittracker.domain.model

import java.time.LocalDate

data class HabitDate(
    val id: Int = 0,
    val habitId: Int,
    val date: LocalDate = LocalDate.now()
)
