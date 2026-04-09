package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.repository.HabitDateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetHabitDatesUseCase(
    private val dateRepository: HabitDateRepository
) {
    suspend operator fun invoke(id : Int, fromDate: LocalDate, toDate: LocalDate): List<LocalDate>{
        return dateRepository.getDatesOfHabitInRange(id,fromDate,toDate)
    }
}