package com.example.habittracker.data.remote

import com.example.habittracker.data.model.FirebaseHabitUnit
import kotlinx.coroutines.flow.Flow

interface HabitRemoteDataSource {
    suspend fun addHabit(userId: String, habit: FirebaseHabitUnit): String?
    suspend fun deleteHabit(userId: String, habitId: String)

    fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>>

    suspend fun updateHabitStreakAndDates(userId: String, habitId: String, streak: Int, dates: List<String>)
}