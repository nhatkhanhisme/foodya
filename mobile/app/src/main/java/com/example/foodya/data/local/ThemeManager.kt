package com.example.foodya.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to create DataStore instance
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

/**
 * ThemeManager - Manages theme preference persistence using DataStore
 * 
 * Responsibilities:
 * - Save and retrieve user's dark mode preference
 * - Provide reactive Flow for theme changes
 * - Handle default theme (system theme)
 */
@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.themeDataStore

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    /**
     * Flow that emits the current dark mode preference
     * Returns null if no preference is set (use system default)
     */
    val isDarkMode: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE]
    }

    /**
     * Save dark mode preference
     * @param isDark true for dark mode, false for light mode
     */
    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    /**
     * Toggle between dark and light mode
     * @param currentMode current dark mode state
     */
    suspend fun toggleDarkMode(currentMode: Boolean) {
        setDarkMode(!currentMode)
    }

    /**
     * Clear theme preference (will use system default)
     */
    suspend fun clearThemePreference() {
        dataStore.edit { preferences ->
            preferences.remove(IS_DARK_MODE)
        }
    }
}
