package com.example.data.data.remote

import com.example.data.data.model.FirebaseHabitUnit
import kotlinx.coroutines.flow.Flow

interface HabitRemoteDataSource {
    suspend fun addHabit(userId: String, habit: FirebaseHabitUnit): String?
    suspend fun deleteHabit(userId: String, habitId: String)

    fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>>

    suspend fun updateHabit(userId: String, habit: FirebaseHabitUnit)
    suspend fun updateHabitsInRTDB(uid: String, habitsToSync: List<FirebaseHabitUnit>)
}