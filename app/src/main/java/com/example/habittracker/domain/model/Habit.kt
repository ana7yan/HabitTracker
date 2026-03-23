package com.example.habittracker.domain.model

data class Habit(
    val id: Int = 0,
    val name: String,
    val streak: Int = 0,
    val lastCompletedDate: String = "",
    val isCompletedToday: Boolean = false,
    val creationDate: String = ""
)
