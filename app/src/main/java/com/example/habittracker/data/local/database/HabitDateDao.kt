package com.example.habittracker.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.habittracker.data.model.HabitDateEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDateDao {

    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM habitdateentity
        WHERE habitId = :habitId
        AND date = :date
    )
""")
    fun isHabitCompletedOnDate(habitId: Int, date: LocalDate): Boolean
    @Upsert
    fun upsertDate(habitDate: HabitDateEntity)

    @Upsert
    fun upsertDates(habitDate: List<HabitDateEntity>)

    @Query("DELETE FROM habitdateentity WHERE habitId LIKE :habitId")
    fun deleteHabit(habitId: Int)

    @Query("""
    SELECT date FROM habitdateentity
    WHERE habitId = :habitId 
    AND date BETWEEN :fromDate AND :toDate
""")
    fun getDatesOfHabitInRange(
        habitId: Int,
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<LocalDate>
    @Query("SELECT date FROM habitdateentity WHERE habitId = :habitId AND date BETWEEN :fromDate AND :toDate")
    fun getDatesOfHabitInRangeAsFlow(
        habitId: Int,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Flow<List<LocalDate>>

    @Query("""
    SELECT date FROM habitdateentity 
    WHERE habitId = :habitId 
""")
    fun getDatesOfHabit(
        habitId: Int
    ): List<String>
}