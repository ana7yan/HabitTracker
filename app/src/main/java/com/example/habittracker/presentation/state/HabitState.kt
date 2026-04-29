package com.example.habittracker.presentation.state

import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.SortType

data class HabitState(
    val habits: List<Habit> = emptyList(),
    val name: String = "",
    val isAddingHabit: Boolean = false,
    val sortType: SortType = SortType.NAME,
    val addingError: String? = null
)