package com.example.habittracker.domain.usecase

import com.example.habittracker.data.model.FirebaseHabitUnit
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncLocalToRemoteUseCase  @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val auth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository
) {
    suspend operator fun invoke() {
        val uid = auth.currentUser?.uid ?: return
        habitRemoteRepository.observeHabits(uid).first().forEach { habitUnit ->
            habitRemoteRepository.deleteHabitFromDB(uid,habitUnit.remoteId)
        }

        habitRepository.getAllHabits().forEach { habit ->
            val dates = habitDateRepository.getAllDates(habit.id)
            val newHabit = FirebaseHabitUnit(name = habit.name, streak = habit.streak, creationDate = habit.creationDate, checkedDates = dates)
            habitRemoteRepository.addHabitToDB(uid,newHabit)
        }
    }
}