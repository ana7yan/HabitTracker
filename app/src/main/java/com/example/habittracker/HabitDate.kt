package com.example.habittracker

import androidx.compose.ui.platform.LocalContext
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    indices = [Index(value = ["habitId","date"], unique = true)]
)
data class HabitDate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitId: Int,
    val date: LocalDate = LocalDate.now()
)
