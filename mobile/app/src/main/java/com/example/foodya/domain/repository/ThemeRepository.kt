package com.example.foodya.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * ThemeRepository - Domain layer interface for theme management
 * 
 * Provides access to theme preferences in a reactive way
 */
interface ThemeRepository {
    /**
     * Flow that emits the current dark mode preference
     * Returns null if no preference is set (use system default)
     */
    val isDarkMode: Flow<Boolean?>

    /**
     * Set dark mode preference
     * @param isDark true for dark mode, false for light mode
     */
    suspend fun setDarkMode(isDark: Boolean)

    /**
     * Toggle between dark and light mode
     * @param currentMode current dark mode state
     */
    suspend fun toggleDarkMode(currentMode: Boolean)

    /**
     * Clear theme preference (revert to system default)
     */
    suspend fun clearThemePreference()
}
