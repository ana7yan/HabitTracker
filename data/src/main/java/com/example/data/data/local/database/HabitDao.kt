package com.example.data.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.data.data.model.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao{
    @Query("Select * from habits")
    suspend fun getAll(): List<HabitEntity>


    /*@Query("SELECT * FROM habits WHERE id IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE name LIKE :habitName LIMIT 1")
    fun findByName(habitName: String): HabitEntity
*/
    @Query("SELECT * FROM habits ORDER BY name ASC")
    fun getHabitsOrderedByName(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY streak DESC")
    fun getHabitsOrderedByStreak(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Int): HabitEntity

    @Update
    suspend fun updateHabit(habit: HabitEntity)
    @Update
    suspend fun updateHabits(habits: List<HabitEntity>)

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Delete
    fun delete(habit: HabitEntity)


}