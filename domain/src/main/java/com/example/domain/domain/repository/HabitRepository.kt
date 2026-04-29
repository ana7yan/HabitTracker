package com.example.domain.domain.repository


import com.example.domain.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun getAllHabits(): List<Habit>

    suspend fun getHabitsOrderedByName(): Flow<List<Habit>>
    suspend fun getHabitsOrderedByStreak(): Flow<List<Habit>>

    suspend fun upsertHabit(habit: Habit)
    suspend fun insertHabit(habit: Habit): Int
    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabits(habits: List<Habit>)



}