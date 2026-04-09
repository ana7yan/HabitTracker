package com.example.habittracker.data.repository

import com.example.habittracker.data.local.preferences.PreferencesManager
import com.example.habittracker.domain.repository.PreferencesRepository

class PreferencesRepositoryImpl (
    private val prefs: PreferencesManager
): PreferencesRepository {

    override suspend fun getLastResetDate(): Long?{
        return prefs.getLastResetDate()
    }

    override suspend fun saveLastResetDate(date: Long) {
        prefs.saveLastResetDate(date)
    }
}