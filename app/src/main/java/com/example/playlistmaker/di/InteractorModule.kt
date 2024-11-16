package com.example.playlistmaker.di

import com.example.playlistmaker.media.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.media.favorites.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.player.domain.Impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.common.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.common.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SharingInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistsInteractorImpl(get())
    }
}