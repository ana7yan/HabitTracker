package com.example.data.data.repository

import com.example.data.data.local.preferences.PreferencesManager
import com.example.domain.domain.repository.PreferencesRepository

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