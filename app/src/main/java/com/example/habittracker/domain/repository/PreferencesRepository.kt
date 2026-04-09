package com.example.habittracker.domain.repository

interface PreferencesRepository {
    suspend fun getLastResetDate(): Long?
    suspend fun saveLastResetDate(date:Long)
}