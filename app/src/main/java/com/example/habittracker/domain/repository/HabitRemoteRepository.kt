package com.example.habittracker.domain.repository

import com.example.habittracker.data.model.FirebaseHabitUnit
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
    suspend fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>>
}