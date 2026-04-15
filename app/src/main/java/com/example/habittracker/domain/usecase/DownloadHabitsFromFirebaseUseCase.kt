package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitDate
import com.example.habittracker.domain.repository.HabitDateRepository
import com.example.habittracker.domain.repository.HabitRemoteRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class DownloadHabitsFromFirebaseUseCase @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val auth: FirebaseAuth,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository
) {
    suspend operator fun invoke() {
        val uid = auth.currentUser?.uid ?: return
        habitRemoteRepository.observeHabits(uid).first().forEach { habitUnit ->
            val habit = Habit(
                remoteId = habitUnit.remoteId,
                name = habitUnit.name,
                streak = habitUnit.streak,
                creationDate = habitUnit.creationDate
            )
            habitRepository.upsertHabit(habit)
            habitUnit.checkedDates.forEach { date ->
                habitDateRepository.upsertDate(HabitDate(
                    habitId = habit.id,
                    date = LocalDate.parse(date)
                ))
            }
        }

    }
}