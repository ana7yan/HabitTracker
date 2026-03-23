package com.example.habittracker.data.repository

import androidx.lifecycle.viewModelScope
import com.example.habittracker.PreferencesManager
import com.example.habittracker.SortType
import com.example.habittracker.data.local.HabitDao
import com.example.habittracker.data.local.HabitDateDao
import com.example.habittracker.data.mapper.toData
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.presentation.state.HabitEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import com.example.habittracker.data.mapper.toDomain
import java.time.LocalDate

class HabitRepositoryImpl(
    private val prefs: PreferencesManager,
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

    override suspend fun upsertHabit(habit: Habit) {
        dao.upsertHabit(habit.toData())
    }

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habit.toData())
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.delete(habit.toData())
    }

    override suspend fun getLastResetDate(): Long?{
        return prefs.getLastResetDate()
    }

    override suspend fun saveLastResetDate(date: Long) {
        prefs.saveLastResetDate(date)
    }
}