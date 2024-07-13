package com.example.playlistmaker.settings.data


import android.app.Application.MODE_PRIVATE
import android.content.Context
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.model.ThemeSettings.Companion.DARK_THEME
import com.example.playlistmaker.settings.domain.model.ThemeSettings.Companion.SETTINGS

class SettingsRepositoryImpl(val context: Context): SettingsRepository {

    private var sharedPreferences = context.applicationContext.getSharedPreferences(
        SETTINGS,
        MODE_PRIVATE
    )

    override fun getThemeSettings(): ThemeSettings {
            val darkTheme = sharedPreferences.getBoolean(DARK_THEME, false)
            return ThemeSettings(darkTheme)
        }

        override fun updateThemeSettings(settings: ThemeSettings) {
            sharedPreferences.edit().putBoolean(DARK_THEME, settings.darkTheme).apply()
        }
}