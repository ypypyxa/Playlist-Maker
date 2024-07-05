package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

private const val SETTINGS = "settings"
private const val DARK_THEME = "dark_theme"

class App : Application() {

    var darkTheme = false
    private lateinit var settings: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        settings = getSharedPreferences(SETTINGS, MODE_PRIVATE)

        if (settings.getBoolean(DARK_THEME, false) != false) {
            darkTheme = settings.getBoolean(DARK_THEME, false)
            switchTheme(darkTheme)
        } else {
            switchTheme(darkTheme)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        settings.edit()
            .putBoolean(DARK_THEME, darkTheme)
            .apply()
    }
}