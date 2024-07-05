package com.example.playlistmaker.settings.domain.model

class ThemeSettings (
    val darkTheme: Boolean
) {
    companion object {
        const val SETTINGS = "settings"
        const val DARK_THEME = "dark_theme"
    }
}