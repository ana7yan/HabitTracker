package com.example.habittracker.presentation.state

import com.example.habittracker.domain.model.SortType
import com.example.habittracker.data.model.HabitEntity
import com.example.habittracker.domain.model.Habit

data class HabitState(
    val habits: List<Habit> = emptyList(),
    val name: String = "",
    val isAddingHabit: Boolean = false,
    val sortType: SortType = SortType.NAME
)