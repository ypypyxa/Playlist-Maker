package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

private lateinit var settingsActivityViewModel: SettingsActivityViewModel

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsActivityViewModel = ViewModelProvider(
            this,
            SettingsActivityViewModel.getViewModelFactory()
        )[SettingsActivityViewModel::class.java]

        val backButton = findViewById<ImageButton>(R.id.ibBack)
        val mailToSupButton = findViewById<ImageButton>(R.id.ibMailToSupport)
        val shareButton = findViewById<ImageButton>(R.id.ibShare)
        val agreementButton = findViewById<ImageButton>(R.id.ibUserAgreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.swTheme)

        themeSwitcher.isChecked = settingsActivityViewModel.getThemeSettings()

        settingsActivityViewModel.getThemeSettingsLiveData().observe(this) {
            (applicationContext as App).switchTheme(it)
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsActivityViewModel.switchTheme(checked)
        }

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            settingsActivityViewModel.shareApp()
        }

        mailToSupButton.setOnClickListener {
            settingsActivityViewModel.sendMail()
        }

        agreementButton.setOnClickListener {
            settingsActivityViewModel.openTherms()
        }
    }
}