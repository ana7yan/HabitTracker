package com.example.habittracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

    @Dao
    interface HabitDao{
        @Query("Select * from habits")
        fun getAll(): List<Habit>


        /*@Query("SELECT * FROM habits WHERE id IN (:habitIds)")
        fun loadAllByIds(habitIds: IntArray): List<Habit>

        @Query("SELECT * FROM habits WHERE name LIKE :habitName LIMIT 1")
        fun findByName(habitName: String): Habit
*/
        @Query("SELECT * FROM habits ORDER BY name ASC")
        fun getHabitsOrderedByName(): Flow<List<Habit>>

        @Query("SELECT * FROM habits ORDER BY streak DESC")
        fun getHabitsOrderedByStreak(): Flow<List<Habit>>

        @Update
        suspend fun updateHabit(habit: Habit)

        @Upsert
        fun upsertHabit(habit: Habit)

        @Delete
        fun delete(user: Habit)


    }
