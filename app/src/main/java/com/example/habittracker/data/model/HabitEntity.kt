package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteId: String? = null,
    val name: String,
    val streak: Int = 0,
    val lastCompletedDate: String? = null,
    val isCompletedToday: Boolean = false,
    val creationDate: String
)