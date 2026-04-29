package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncLocalToRemoteUseCase @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val authRepository: UserAuthRepository,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository,
) {
    suspend operator fun invoke() {
        val uid = authRepository.getCurrentUserId()
        if (uid == null) return
        habitRemoteRepository.observeHabits(uid).first().forEach { habitUnit ->
            habitRemoteRepository.deleteHabitFromDB(uid, habitUnit.remoteId.toString())
        }
        val updatedHabits = mutableListOf<Habit>()
        habitRepository.getAllHabits().forEach { habit ->
            val dates = habitDateRepository.getAllDates(habit.id)

            val remoteId = habitRemoteRepository.addHabitToDB(uid, habit.copy(checkedDates = dates))
            updatedHabits.add(habit.copy(remoteId = remoteId))
        }
        habitRepository.updateHabits(updatedHabits)
    }
}