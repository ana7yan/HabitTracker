package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.PreferencesRepository
import java.time.LocalDate
import javax.inject.Inject

class CheckAndResetHabitForNewDayUseCase @Inject constructor (
    private val repository: HabitRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke() {
        val today = LocalDate.now().toEpochDay()
        val lastReset = preferencesRepository.getLastResetDate()

        if (lastReset == today) return

        val habits = repository.getAllHabits()

        habits.forEach { habit ->
            val updatedHabit = handleHabitReset(habit)
            repository.updateHabit(updatedHabit)
        }

        preferencesRepository.saveLastResetDate(today)
    }

    private fun handleHabitReset(habit: Habit): Habit {
        val today = LocalDate.now()
        val lastCompleted = habit.lastCompletedDate

        return if (lastCompleted == today.minusDays(2).toString()) {
            habit.copy(
                isCompletedToday = false,
                streak = 0
            )
        } else {
            habit.copy(
                isCompletedToday = false
            )
        }
    }
}