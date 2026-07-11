package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.PreferencesRepository
import com.example.domain.domain.repository.UserAuthRepository
import java.nio.channels.NetworkChannel
import java.time.LocalDate
import javax.inject.Inject

class CheckAndResetHabitForNewDayUseCase @Inject constructor (
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository,
    private val preferencesRepository: PreferencesRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository
) {
    suspend operator fun invoke() {
        val today = LocalDate.now().toEpochDay()
        val lastReset = preferencesRepository.getLastResetDate()

        if (lastReset == today) return

        val habits = repository.getAllHabits()
        val userId = authRepository.getCurrentUserId()

        habits.forEach { habit ->
            val updatedHabit = handleHabitReset(habit)
            repository.updateHabit(updatedHabit)
            if (userId != null) {
                val dates = dateRepository.getAllDates(habit.id)
                firebaseRepository.updateHabitInDB(
                    userId,
                    updatedHabit.copy(checkedDates = dates)
                )
            }
        }

        preferencesRepository.saveLastResetDate(today)
    }

    private fun handleHabitReset(habit: Habit): Habit {
        val today = LocalDate.now()
        val lastCompleted = habit.lastCompletedDate

        return if (lastCompleted == today.minusDays(1).toString()) {
            habit.copy(
                isCompletedToday = false
            )
        } else {
            habit.copy(
                isCompletedToday = false,
                streak = 0
            )
        }
    }
}