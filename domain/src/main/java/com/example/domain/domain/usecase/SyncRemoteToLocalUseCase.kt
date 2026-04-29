package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.HabitDate
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class SyncRemoteToLocalUseCase @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val authRepository: UserAuthRepository,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository
) {
    suspend operator fun invoke() {
        val uid = authRepository.getCurrentUserId()
        if(uid == null) return
        habitRepository.getAllHabits().forEach { habit ->
            habitRepository.deleteHabit(habit)
            habitDateRepository.deleteHabit(habit.id)
        }

        habitRemoteRepository.observeHabits(uid).first().forEach { remoteHabit ->
            val lastCompetedDates = remoteHabit.checkedDates.maxOfOrNull { LocalDate.parse(it) }.toString()
            val isCompletedToday = remoteHabit.checkedDates.contains(LocalDate.now().toString())
            val habit = Habit(
                remoteId = remoteHabit.remoteId,
                name = remoteHabit.name,
                streak = remoteHabit.streak,
                isCompletedToday = isCompletedToday,
                lastCompletedDate = lastCompetedDates,
                creationDate = remoteHabit.creationDate,
            )
            val habitId = habitRepository.insertHabit(habit)
            val dates = remoteHabit.checkedDates.map {
                HabitDate(habitId = habitId, date = LocalDate.parse(it))
            }
            habitDateRepository.upsertDates(dates)
        }

    }
}