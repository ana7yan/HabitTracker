package com.example.data.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "habits",
    indices = [Index(value = ["name"], unique = true)]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteId: String? = null,
    val name: String,
    val streak: Int = 0,
    val lastCompletedDate: String? = null,
    val isCompletedToday: Boolean = false,
    val creationDate: String,
    val hasReminder: Boolean = false,
    val reminderHour: Int = 0,
    val reminderMinute: Int = 0
)