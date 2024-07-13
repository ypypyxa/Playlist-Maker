package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class App : Application() {

    private var darkTheme = ThemeSettings(false)
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor = Creator.provideSettingsInteractor(this.applicationContext)

        if (settingsInteractor.getThemeSettings().darkTheme) {
            darkTheme = settingsInteractor.getThemeSettings()
            switchTheme(darkTheme)
        } else {
            switchTheme(darkTheme)
        }
    }

    fun switchTheme(darkThemeEnabled: ThemeSettings) {

        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled.darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        settingsInteractor.updateThemeSettings(darkTheme)
    }
}