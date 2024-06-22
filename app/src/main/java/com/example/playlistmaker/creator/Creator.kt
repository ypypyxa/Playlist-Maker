package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.presentation.PlayerActivityViewModel
import com.example.playlistmaker.player.presentation.PlayerView
import com.example.playlistmaker.history.data.HistoryRepositoryImpl
import com.example.playlistmaker.history.domain.api.HistoryInteractor
import com.example.playlistmaker.history.domain.api.HistoryRepository
import com.example.playlistmaker.history.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(context)
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context))
    }

    fun provideMediaPlayer(): MediaPlayerInteractor {
        return MediaPlayerInteractor(getMediaPlayerRepository())
    }

    fun providePlayerActivityViewModel(
        view: PlayerView,
        context: Context
    ): PlayerActivityViewModel {
        return PlayerActivityViewModel(
            playerView = view,
            context = context
        )
    }
}