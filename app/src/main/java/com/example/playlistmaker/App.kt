package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val SETTINGS = "settings"
const val DARK_THEME = "dark_theme"

class App : Application() {

    var darkTheme = false
    lateinit var settings: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        settings = getSharedPreferences(SETTINGS, MODE_PRIVATE)

        if (settings.getString(DARK_THEME, "") != "") {
            darkTheme = settings.getString(DARK_THEME, "").toBoolean()
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
            .putString(DARK_THEME, darkTheme.toString())
            .apply()
    }
}