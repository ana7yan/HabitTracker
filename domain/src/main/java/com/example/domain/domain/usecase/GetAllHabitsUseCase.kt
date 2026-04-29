package com.example.domain.domain.usecase

import com.example.domain.domain.model.Habit
import com.example.domain.domain.model.SortType
import com.example.domain.domain.repository.HabitDateRepository
import com.example.domain.domain.repository.HabitRepository
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