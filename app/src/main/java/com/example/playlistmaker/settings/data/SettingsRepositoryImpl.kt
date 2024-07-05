package com.example.playlistmaker.settings.data


import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.model.ThemeSettings.Companion.DARK_THEME

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {
        override fun getThemeSettings(): ThemeSettings {
            val darkTheme = sharedPreferences.getBoolean(DARK_THEME, false)
            return ThemeSettings(darkTheme)
        }

        override fun updateThemeSettings(settings: ThemeSettings) {
            sharedPreferences.edit().putBoolean(DARK_THEME, settings.darkTheme).apply()
        }
}