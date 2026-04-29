package com.example.data.data.repository

import com.example.data.data.mapper.toDataRemote
import com.example.data.data.mapper.toDomainRemote
import com.example.data.data.model.FirebaseHabitUnit
import com.example.data.data.remote.FirebaseHabitDataSource
import com.example.domain.domain.model.Habit
import com.example.domain.domain.repository.HabitRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HabitRemoteRepositoryImpl(
    private val firebaseHabitDataSource: FirebaseHabitDataSource
) : HabitRemoteRepository{
    override suspend fun addHabitToDB(
        userId: String,
        habit: Habit,
    ): String {
        return firebaseHabitDataSource.addHabit(userId,habit.toDataRemote()) ?: ""
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

    override suspend fun updateHabitsInRTDB(
        userId: String,
        habitsToSync: List<Habit>,
    ) {
        val firebaseHabitUnits = habitsToSync.map{ habitToSync->
            FirebaseHabitUnit(
                name = habitToSync.name,
                remoteId = habitToSync.remoteId.toString(),
                streak = habitToSync.streak,
                creationDate = habitToSync.creationDate,
                checkedDates = habitToSync.checkedDates
            )
        }

        firebaseHabitDataSource.updateHabitsInRTDB(uid = userId,firebaseHabitUnits)
    }

    override suspend fun observeHabits(userId: String): Flow<List<Habit>> {
        return firebaseHabitDataSource.observeHabits(userId).map {list-> list.map { it.toDomainRemote() } }
    }
    override suspend fun getHabits(userId: String): List<Habit> {
        val habits = firebaseHabitDataSource.observeHabits(userId).first().map { habitUnit ->
            Habit(
                remoteId = habitUnit.remoteId,
                name = habitUnit.name,
                streak = habitUnit.streak,
                creationDate = habitUnit.creationDate
            )
        }
        return habits
    }

}