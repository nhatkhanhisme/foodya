package com.example.foodya.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ThemeViewModel - Manages global theme state for the entire app
 * 
 * This ViewModel should be scoped to MainActivity to ensure theme persistence
 * across the entire app lifecycle.
 * 
 * Usage:
 * ```
 * val themeViewModel: ThemeViewModel by viewModels()
 * val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
 * FoodyaTheme(darkTheme = isDarkTheme) { ... }
 * ```
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    /**
     * StateFlow that emits the current theme state
     * - Returns user preference if set
     * - Falls back to system theme if no preference is saved
     * 
     * This is exposed as StateFlow for efficient composition recomposition
     */
    val isDarkTheme: StateFlow<Boolean> = themeRepository.isDarkMode
        .map { savedPreference ->
            // If user hasn't set a preference, use system default
            // Note: isSystemInDarkTheme() can't be called here (not in Composable)
            // So we'll use false as default and handle system check in MainActivity
            savedPreference ?: false // Will be handled properly in MainActivity
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false // Temporary initial value
        )

    /**
     * StateFlow that emits null if no preference is set (for system default check)
     */
    val themePreference: StateFlow<Boolean?> = themeRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Toggle between dark and light mode
     * @param currentMode current dark mode state
     */
    fun toggleDarkMode(currentMode: Boolean) {
        viewModelScope.launch {
            themeRepository.toggleDarkMode(currentMode)
        }
    }

    /**
     * Set dark mode explicitly
     * @param isDark true for dark mode, false for light mode
     */
    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkMode(isDark)
        }
    }

    /**
     * Clear theme preference (revert to system default)
     */
    fun clearThemePreference() {
        viewModelScope.launch {
            themeRepository.clearThemePreference()
        }
    }
}

/**
 * Helper function to determine if dark theme should be used
 * Takes into account user preference and system default
 */
@Composable
fun rememberThemePreference(
    themePreference: Boolean?,
    systemInDarkTheme: Boolean = isSystemInDarkTheme()
): Boolean {
    return themePreference ?: systemInDarkTheme
}
