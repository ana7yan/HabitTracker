package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitDateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitId: Int,
    val date: LocalDate = LocalDate.now()
)