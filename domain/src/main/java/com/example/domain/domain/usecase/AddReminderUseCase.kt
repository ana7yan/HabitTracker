package com.example.domain.domain.usecase

import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.example.domain.domain.sceduler.ReminderScheduler
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(habitId: Int,reminderHour: Int, reminderMinute: Int){
        val habit = repository.getHabitById(habitId)
        val updatedHabit = habit.copy(
            hasReminder = true,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute
        )
        repository.updateHabit(updatedHabit)
        val uid = authRepository.getCurrentUserId()
        if(uid != null){
            firebaseRepository.updateHabitInDB(uid, updatedHabit)
        }
        reminderScheduler.scheduleReminder(
            updatedHabit
        )
    }
}