package com.example.domain.domain.repository

import com.example.domain.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRemoteRepository {
    suspend fun addHabitToDB(
        userId: String,
        habit: Habit
    ): String
    suspend fun deleteHabitFromDB(
        userId: String,
        habitId: String
    )
    suspend fun updateHabitInDB(
        userId: String,
        habitId: String,
        streak: Int,
        dates: List<String>,
    )
    suspend fun updateHabitsInRTDB(
        userId: String,
        habitsToSync: List<Habit>
    )
    suspend fun observeHabits(userId: String): Flow<List<Habit>>
    suspend fun getHabits(userId: String): List<Habit>
}