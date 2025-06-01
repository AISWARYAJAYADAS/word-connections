package com.aiswarya.wordconnections.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aiswarya.wordconnections.domain.model.GameSettings
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val gameSettings = dataStore.data.map { preferences ->
        GameSettings(
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            hapticEnabled = preferences[HAPTIC_ENABLED] ?: true,
            darkMode = preferences[DARK_MODE] ?: false
        )
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateHapticEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
}