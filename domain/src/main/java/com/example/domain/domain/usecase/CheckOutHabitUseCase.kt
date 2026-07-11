package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.HabitDate
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import java.time.LocalDate
import javax.inject.Inject

class CheckOutHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository,
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
        val dates = dateRepository.getAllDates(habitUpdated.id)
        val userId = authRepository.getCurrentUserId() ?: return
        firebaseRepository.updateHabitInDB(
            userId,
            habitUpdated.copy(checkedDates = dates)
        )
    }
}