package com.example.foodya.data.repository

import com.example.foodya.data.local.ThemeManager
import com.example.foodya.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ThemeRepositoryImpl - Implementation of ThemeRepository
 * 
 * Delegates to ThemeManager for DataStore operations
 */
@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val themeManager: ThemeManager
) : ThemeRepository {

    override val isDarkMode: Flow<Boolean?> = themeManager.isDarkMode

    override suspend fun setDarkMode(isDark: Boolean) {
        themeManager.setDarkMode(isDark)
    }

    override suspend fun toggleDarkMode(currentMode: Boolean) {
        themeManager.toggleDarkMode(currentMode)
    }

    override suspend fun clearThemePreference() {
        themeManager.clearThemePreference()
    }
}
