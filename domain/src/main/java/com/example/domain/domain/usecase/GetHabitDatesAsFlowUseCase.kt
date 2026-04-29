package com.example.domain.domain.usecase

import com.example.domain.domain.repository.HabitDateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetHabitDatesAsFlowUseCase @Inject constructor (
    private val dateRepository: HabitDateRepository
) {
    operator fun invoke(id : Int): Flow<List<LocalDate>>{
        val sevenDaysAgo = LocalDate.now().minusDays(7)
        val today = LocalDate.now()
        return dateRepository.getDatesOfHabitInRangeAsFlow(id,sevenDaysAgo,today)
    }
}