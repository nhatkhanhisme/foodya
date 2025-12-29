package com.example.foodya.ui.common

/**
 * Sealed class representing one-time UI events (not state)
 * Used for showing transient feedback like Snackbars, Toasts, Navigation events
 */
sealed class UiEvent {
    /**
     * Show a snackbar message to the user
     * @param message The user-friendly message to display
     * @param isError Whether this is an error message (for styling)
     */
    data class ShowSnackbar(
        val message: String,
        val isError: Boolean = false
    ) : UiEvent()
    
    /**
     * Navigate to a destination (can be extended for navigation events)
     */
    data class Navigate(val route: String) : UiEvent()
}
