package com.example.habittracker.data.repository

import com.example.habittracker.data.model.FirebaseHabitUnit
import com.example.habittracker.data.remote.FirebaseHabitDataSource
import com.example.habittracker.domain.repository.HabitRemoteRepository
import kotlinx.coroutines.flow.Flow

class HabitRemoteRepositoryImpl(
    private val firebaseHabitDataSource: FirebaseHabitDataSource
) : HabitRemoteRepository{
    override suspend fun addHabitToDB(
        userId: String,
        habit: FirebaseHabitUnit,
    ): String {
        return firebaseHabitDataSource.addHabit(userId,habit) ?: ""
    }

    override suspend fun deleteHabitFromDB(userId: String, habitId: String) {
        firebaseHabitDataSource.deleteHabit(userId,habitId)
    }

    override suspend fun updateHabitInDB(
        userId: String,
        habitId: String,
        streak: Int,
        dates: List<String>
    ) {
        firebaseHabitDataSource.updateHabitStreakAndDates(
            userId,
            habitId,
            streak,
            dates
        )
    }

    override suspend fun observeHabits(userId: String): Flow<List<FirebaseHabitUnit>> {
        return firebaseHabitDataSource.observeHabits(userId)
    }

}