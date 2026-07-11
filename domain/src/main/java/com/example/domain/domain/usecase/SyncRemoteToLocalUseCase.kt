package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.HabitDate
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRemoteRepository
import com.example.domain.domain.repository.HabitRepository
import com.example.domain.domain.repository.UserAuthRepository
import com.example.domain.domain.sceduler.ReminderScheduler
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

class SyncRemoteToLocalUseCase @Inject constructor(
    private val habitRemoteRepository: HabitRemoteRepository,
    private val authRepository: UserAuthRepository,
    private val habitRepository: HabitRepository,
    private val habitDateRepository: HabitDateRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        val uid = authRepository.getCurrentUserId()
        if(uid == null) return
        habitRepository.getAllHabits().forEach { habit ->
            habitRepository.deleteHabit(habit)
            habitDateRepository.deleteHabit(habit.id)
        }

        habitRemoteRepository.observeHabits(uid).first().forEach { remoteHabit ->
            val lastCompetedDate = remoteHabit.checkedDates.maxOfOrNull { LocalDate.parse(it) }.toString()
            val isCompletedToday = remoteHabit.checkedDates.contains(LocalDate.now().toString())

            val habit = remoteHabit.copy(
                lastCompletedDate = lastCompetedDate,
                isCompletedToday = isCompletedToday
            )
            val habitId = habitRepository.insertHabit(habit)
            val dates = remoteHabit.checkedDates.map {
                HabitDate(habitId = habitId, date = LocalDate.parse(it))
            }
            habitDateRepository.upsertDates(dates)
            if(remoteHabit.hasReminder){
                reminderScheduler.scheduleReminder(
                    remoteHabit.copy(id = habitId)
                )
            }
        }

    }
}