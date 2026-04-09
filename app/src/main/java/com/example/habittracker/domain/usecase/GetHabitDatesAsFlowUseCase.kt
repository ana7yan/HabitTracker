package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.repository.HabitDateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetHabitDatesAsFlowUseCase(
    private val dateRepository: HabitDateRepository
) {
    operator fun invoke(id : Int): Flow<List<LocalDate>>{
        val sevenDaysAgo = LocalDate.now().minusDays(7)
        val today = LocalDate.now()
        return dateRepository.getDatesOfHabitInRangeAsFlow(id,sevenDaysAgo,today)
    }
}