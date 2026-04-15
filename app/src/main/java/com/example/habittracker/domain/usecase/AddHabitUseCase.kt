package com.example.habittracker.domain.usecase

import com.example.habittracker.data.model.FirebaseHabitUnit
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.repository.UserAuthRepository
import javax.inject.Inject

class AddHabitUseCase @Inject constructor (
    private val repository: HabitRepository,
    private val authRepository: UserAuthRepository,
    private val firebaseRepository: HabitRemoteRepository
){
    suspend operator fun invoke(habit: Habit){
        val uid = authRepository.getCurrentUserId()
        if(uid != null){
            val remoteHabit = FirebaseHabitUnit(
                name = habit.name,
                streak = habit.streak,
                creationDate = habit.creationDate,
                checkedDates = emptyList()
            )
            val remoteId = firebaseRepository.addHabitToDB(uid,remoteHabit)
            val updatedHabit = habit.copy(
                remoteId = remoteId
            )
            repository.upsertHabit(updatedHabit)
            return
        }
        repository.upsertHabit(habit)
    }
}