package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.PlayerActivityViewModel
import com.example.playlistmaker.search.ui.SearchActivityViewModel
import com.example.playlistmaker.settings.ui.SettingsActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerActivityViewModel(get(), get(), get(), androidApplication())
    }

    viewModel {
        SearchActivityViewModel(get(), get(), androidApplication())
    }

    viewModel {
        SettingsActivityViewModel(get(), get(), androidApplication())
    }
}