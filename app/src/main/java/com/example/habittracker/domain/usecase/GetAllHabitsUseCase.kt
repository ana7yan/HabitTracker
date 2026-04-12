package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.SortType
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllHabitsUseCase @Inject constructor (
    private val repository: HabitRepository
) {
    suspend operator fun invoke(sortType: SortType): Flow<List<Habit>> {
        return when (sortType) {
            SortType.NAME -> repository.getHabitsOrderedByName()
            SortType.STREAK -> repository.getHabitsOrderedByStreak()
        }
    }
    suspend operator fun invoke(): List<Habit> {
        return repository.getAllHabits()
    }
}