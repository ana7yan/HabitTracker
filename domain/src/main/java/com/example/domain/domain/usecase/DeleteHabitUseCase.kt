package com.example.domain.domain.usecase


import com.example.domain.domain.model.Habit
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.example.domain.domain.sceduler.ReminderScheduler
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor (
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository,
    private val reminderScheduler: ReminderScheduler
){
    suspend operator fun invoke(habit: Habit){
        repository.deleteHabit(habit)
        dateRepository.deleteHabit(habit.id)
        reminderScheduler.cancelReminder(habit.id.toLong())
        val uid = authRepository.getCurrentUserId() ?: return
        firebaseRepository.deleteHabitFromDB(uid,habit.remoteId.toString())
    }
}