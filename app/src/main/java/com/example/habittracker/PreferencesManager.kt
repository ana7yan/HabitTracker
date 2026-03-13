package com.example.habittracker

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = "habit_prefs"
)

class PreferencesManager(private val context: Context) {

    companion object {
        val LAST_RESET_DATE = longPreferencesKey("last_reset_date")
    }

    suspend fun getLastResetDate(): Long? {
        return context.dataStore.data.first()[LAST_RESET_DATE]
    }

    suspend fun saveLastResetDate(epochDay: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_RESET_DATE] = epochDay
        }
    }
}
