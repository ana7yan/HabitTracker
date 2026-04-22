package com.example.habittracker.data.repository

import com.example.habittracker.data.local.database.HabitDao
import com.example.habittracker.data.mapper.toData
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.habittracker.data.mapper.toDomain
import com.example.habittracker.data.remote.FirebaseHabitDataSource

class HabitRepositoryImpl(
    private val dao: HabitDao
) : HabitRepository {

    override suspend fun getAllHabits(): List<Habit> {
        return dao.getAll().map {
            it.toDomain()
        }
    }

    override suspend fun getHabitsOrderedByName(): Flow<List<Habit>> {
        return dao.getHabitsOrderedByName()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getHabitsOrderedByStreak(): Flow<List<Habit>> {
        return dao.getHabitsOrderedByStreak()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsertHabit(habit: Habit){
        dao.upsertHabit(habit.toData())
    }

    override suspend fun insertHabit(habit: Habit): Int {
        return dao.insertHabit(habit.toData()).toInt()
    }

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habit.toData())
    }
    override suspend fun updateHabits(habit: List<Habit>) {
        dao.updateHabits(habit.map { it.toData() })
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.delete(habit.toData())
    }

}