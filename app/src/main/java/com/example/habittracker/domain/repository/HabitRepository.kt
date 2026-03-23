package com.example.habittracker.domain.repository

import com.example.habittracker.SortType
import com.example.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface HabitRepository {
    suspend fun getAllHabits(): List<Habit>

    suspend fun getHabitsOrderedByName(): Flow<List<Habit>>
    suspend fun getHabitsOrderedByStreak(): Flow<List<Habit>>

    suspend fun upsertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)
    suspend fun getLastResetDate(): Long?
    suspend fun saveLastResetDate(date:Long)


}