package com.example.domain.domain.usecase

import com.example.domain.domain.repository.HabitDateRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHabitDatesUseCase @Inject constructor (
    private val dateRepository: HabitDateRepository
) {
    suspend operator fun invoke(id : Int, fromDate: LocalDate, toDate: LocalDate): List<LocalDate>{
        return dateRepository.getDatesOfHabitInRange(id,fromDate,toDate)
    }
}