package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository

class AddHabitUseCase (
    private val repository: HabitRepository
){
    suspend operator fun invoke(habit: Habit){
        repository.upsertHabit(habit)
    }
}