package com.example.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.common.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.favorites.data.FavoritesRepositoryImpl
import com.example.playlistmaker.media.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.common.data.converters.TrackDbConvertor
import com.example.playlistmaker.common.data.PlaylistRepositoryImpl
import com.example.playlistmaker.common.domain.api.PlaylistsRepository
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.common.data.HistoryRepositoryImpl
import com.example.playlistmaker.common.data.HistoryRepositoryImpl.Companion.HISTORY
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.common.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SharingRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.api.SharingRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings.Companion.SETTINGS
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        SearchRepositoryImpl(get(), get(), get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl()
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(androidContext().getSharedPreferences(HISTORY, MODE_PRIVATE))
    }

    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext().getSharedPreferences(SETTINGS, MODE_PRIVATE))
    }

    factory { TrackDbConvertor() }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConverter() }
    single<PlaylistsRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
}