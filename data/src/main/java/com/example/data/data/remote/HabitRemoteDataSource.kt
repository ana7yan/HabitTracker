package com.example.data.data.remote

import com.example.data.data.model.FirebaseHabitUnit
import kotlinx.coroutines.flow.Flow

interface HabitRemoteDataSource {
    suspend fun addHabit(userId: String, habit: FirebaseHabitUnit): String?
    suspend fun deleteHabit(userId: String, habitId: String)

    fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>>

    suspend fun updateHabitStreakAndDates(userId: String, habitId: String, streak: Int, dates: List<String>)
    suspend fun updateHabitsInRTDB(uid: String, habitsToSync: List<FirebaseHabitUnit>)
}