package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.example.domain.domain.sceduler.ReminderScheduler
import java.util.Calendar
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(habit: Habit) {
        val uid = authRepository.getCurrentUserId()
        val updatedHabit: Habit
        if (uid != null) {
            val remoteHabit = Habit(
                name = habit.name,
                streak = habit.streak,
                creationDate = habit.creationDate,
                checkedDates = emptyList(),
                hasReminder = habit.hasReminder,
                reminderHour = habit.reminderHour,
                reminderMinute = habit.reminderMinute
            )
            val remoteId = firebaseRepository.addHabitToDB(uid, remoteHabit)
            updatedHabit = habit.copy(
                remoteId = remoteId
            )
        } else {
            updatedHabit = habit
        }
        if (updatedHabit.hasReminder) {
            reminderScheduler.scheduleReminder(
                updatedHabit
            )
        }
        repository.upsertHabit(updatedHabit)
    }
}