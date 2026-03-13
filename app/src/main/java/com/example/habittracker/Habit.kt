package com.example.habittracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val streak: Int = 0,
    val lastCompletedDate: String? = null,
    val isCompletedToday: Boolean = false,
    val creationDate: String
)
