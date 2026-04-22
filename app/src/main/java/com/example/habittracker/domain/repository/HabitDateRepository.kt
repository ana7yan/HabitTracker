package com.example.habittracker.domain.repository

import com.example.habittracker.domain.model.HabitDate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitDateRepository {
    suspend fun getDatesOfHabitInRange(habitId:Int, fromDate: LocalDate, toDate: LocalDate): List<LocalDate>
    suspend fun upsertDate(habitDate: HabitDate)
    suspend fun upsertDates(habitDate: List<HabitDate>)
    fun getDatesOfHabitInRangeAsFlow(id: Int, sevenDaysAgo: LocalDate, today: LocalDate): Flow<List<LocalDate>>
    fun deleteHabit(habitId: Int)
    suspend fun getAllDates(id: Int): List<String>
}