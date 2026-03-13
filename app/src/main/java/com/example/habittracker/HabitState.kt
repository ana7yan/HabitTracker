package com.example.habittracker

data class HabitState(
    val habits: List<Habit> = emptyList(),
    val name: String = "",
    val isAddingHabit: Boolean = false,
    val sortType: SortType = SortType.NAME
)
