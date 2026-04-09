package com.example.habittracker.data.repository

import androidx.datastore.dataStore
import com.example.habittracker.data.local.database.HabitDateDao
import com.example.habittracker.domain.model.HabitDate
import com.example.habittracker.domain.repository.HabitDateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.example.habittracker.data.mapper.toData
import com.example.habittracker.data.mapper.toDomain

class HabitDateRepositoryImpl(
    private val dateDao: HabitDateDao
): HabitDateRepository {

    override suspend fun getDatesOfHabitInRange(
        habitId: Int,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): List<LocalDate> {
        return dateDao.getDatesOfHabitInRange(habitId,fromDate,toDate)
    }

    override suspend fun upsertDate(habitDate: HabitDate) {
        dateDao.upsertDate(habitDate.toData())
    }

    override fun getDatesOfHabitInRangeAsFlow(
        id: Int,
        sevenDaysAgo: LocalDate,
        today: LocalDate,
    ): Flow<List<LocalDate>> {
        return dateDao.getDatesOfHabitInRangeAsFlow(id,sevenDaysAgo,today)
    }

    override fun deleteHabit(habitId: Int) {
        dateDao.deleteHabit(habitId)
    }

}