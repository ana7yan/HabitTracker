package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitDate
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRepository
import java.time.LocalDate

class CheckOutHabitUseCase(
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository,
) {
    suspend operator fun invoke(habit: Habit) {
        val habitUpdated = habit.copy(
            streak = habit.streak + 1,
            lastCompletedDate = LocalDate.now().toString(),
            isCompletedToday = true
        )
        repository.updateHabit(habitUpdated)
        dateRepository.upsertDate(
            HabitDate(
                habitId = habit.id, date = LocalDate.now()
            )
        )
    }
}