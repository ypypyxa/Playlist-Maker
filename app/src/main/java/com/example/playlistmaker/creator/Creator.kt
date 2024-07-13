package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.Impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.data.Favorites
import com.example.playlistmaker.search.data.HistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SharingRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ExternalNavigator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.api.SharingInteractor
import com.example.playlistmaker.settings.domain.api.SharingRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(context),
            Favorites(context.getSharedPreferences(Favorites.FAVORITES, Context.MODE_PRIVATE))
        )
    }
    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(context.getSharedPreferences(HistoryRepositoryImpl.HISTORY, Context.MODE_PRIVATE))
    }
    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context))
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }
    fun provideMediaPlayer(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    private fun getExternalNavigatorImpl(context: Context) : ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
    private fun getSharingRepository(context: Context) : SharingRepository {
        return SharingRepositoryImpl(context)
    }
    fun provideSharingInteractor(context: Context) : SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigatorImpl(context),
            getSharingRepository(context)
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}