package com.example.habittracker.domain.repository

import com.example.habittracker.data.model.FirebaseHabitUnit
import com.example.habittracker.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRemoteRepository {
    suspend fun addHabitToDB(
        userId: String,
        habit: FirebaseHabitUnit
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
    suspend fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>>
    suspend fun getHabits(userId: String): List<Habit>
}