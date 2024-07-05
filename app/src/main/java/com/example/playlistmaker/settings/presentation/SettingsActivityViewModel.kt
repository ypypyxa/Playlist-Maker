package com.example.playlistmaker.settings.presentation


import android.app.Application
import android.app.Application.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.model.ThemeSettings.Companion.SETTINGS

class SettingsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var sharedPreferences = application.applicationContext.getSharedPreferences(
        SETTINGS,
        MODE_PRIVATE
    )

    private val shareLiveData = MutableLiveData<Unit>()
    private val themeSettings = MutableLiveData<ThemeSettings>()
    private val themeSettingsLiveData : LiveData<ThemeSettings> = themeSettings

    private val sharingInteractor = Creator.provideSharingInteractor(getApplication())
    private val settingsInteractor = Creator.provideSettingsInteractor(sharedPreferences)

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


    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsActivityViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}