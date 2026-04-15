package com.example.habittracker.domain.usecase


import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.UserAuthRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor (
    private val repository: HabitRepository,
    private val dateRepository: HabitDateRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository,
){
    suspend operator fun invoke(habit: Habit){
        repository.deleteHabit(habit)
        dateRepository.deleteHabit(habit.id)
        val uid = authRepository.getCurrentUserId() ?: return
        firebaseRepository.deleteHabitFromDB(uid,habit.remoteId.toString())
    }
}