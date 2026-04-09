package com.example.habittracker.presentation.state

import com.example.habittracker.domain.model.SortType
import com.example.habittracker.data.model.HabitEntity
import com.example.habittracker.domain.model.Habit

sealed interface HabitEvent {
    object SaveHabit: HabitEvent
    data class SetName(val name: String): HabitEvent
    object ShowDialog: HabitEvent
    object HideDialog: HabitEvent
    data class SortHabits (val sortType: SortType): HabitEvent
    data class DeleteHabit (val habit: Habit): HabitEvent
    data class CheckOutHabit (val habit: Habit): HabitEvent
}