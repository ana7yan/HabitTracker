package com.example.habittracker.domain.usecase


import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor (
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository
){
    suspend operator fun invoke(habit: Habit){
        repository.deleteHabit(habit)
        dateRepository.deleteHabit(habit.id)
    }
}