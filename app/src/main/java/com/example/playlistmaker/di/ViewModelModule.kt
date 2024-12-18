package com.example.playlistmaker.di

import com.example.playlistmaker.media.favorites.ui.FavoritesViewModel
import com.example.playlistmaker.media.create.ui.CreatePlaylistViewModel
import com.example.playlistmaker.media.edit.ui.EditPlaylistViewModel
import com.example.playlistmaker.media.playlist.ui.PlaylistViewModel
import com.example.playlistmaker.media.playlists.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get(), get(), get(), androidApplication())
    }

    viewModel {
        SearchViewModel(get(), get(), androidApplication())
    }

    viewModel {
        SettingsViewModel(get(), get(), androidApplication())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get(), get())
    }

    viewModel {
        PlaylistViewModel(get(), get(), get())
    }

    viewModel {
        EditPlaylistViewModel(get(), get())
    }
}