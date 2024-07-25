package com.example.playlistmaker.settings.data


import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.model.ThemeSettings.Companion.DARK_THEME

class SettingsRepositoryImpl(val settingsPreferences: SharedPreferences): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
            val darkTheme = settingsPreferences.getBoolean(DARK_THEME, false)
            return ThemeSettings(darkTheme)
        }

        override fun updateThemeSettings(settings: ThemeSettings) {
            settingsPreferences.edit().putBoolean(DARK_THEME, settings.darkTheme).apply()
        }
}