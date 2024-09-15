package com.example.playlistmaker.settings.ui


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SharingInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    application: Application
) : AndroidViewModel(application) {

    private val shareLiveData = MutableLiveData<Unit>()
    private val themeSettings = MutableLiveData<ThemeSettings>()
    private val themeSettingsLiveData : LiveData<ThemeSettings> = themeSettings

    fun shareApp() = shareLiveData.postValue(sharingInteractor.shareApp())
    fun sendMail() = sharingInteractor.sendEmail()
    fun openTherms() = sharingInteractor.openTerms()
    fun getThemeSettingsLiveData(): LiveData<ThemeSettings> = themeSettingsLiveData
    fun switchTheme(darkTheme: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(darkTheme))
        themeSettings.value = settingsInteractor.getThemeSettings()
    }
    fun getThemeSettings(): Boolean {
        themeSettings.value = settingsInteractor.getThemeSettings()
        return themeSettings.value?.darkTheme ?: false
    }
}