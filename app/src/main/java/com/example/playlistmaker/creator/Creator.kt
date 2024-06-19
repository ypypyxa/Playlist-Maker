package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.presentation.PlayerActivityPresenter
import com.example.playlistmaker.player.presentation.PlayerView
import com.example.playlistmaker.history.data.HistoryRepositoryImpl
import com.example.playlistmaker.history.domain.api.HistoryInteractor
import com.example.playlistmaker.history.domain.api.HistoryRepository
import com.example.playlistmaker.history.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.presentation.SearchActivityPresenter
import com.example.playlistmaker.search.presentation.SearchView

object Creator {
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    private fun getHistoryRepository(history: SharedPreferences): HistoryRepository {
        return HistoryRepositoryImpl(history)
    }

    fun provideHistoryInteractor(history: SharedPreferences): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(history))
    }

    fun provideMediaPlayer(): MediaPlayerInteractor {
        return MediaPlayerInteractor(getMediaPlayerRepository())
    }

    fun provideSearchActivityPresenter(context: Context): SearchActivityPresenter {
        return SearchActivityPresenter(context = context) }

    fun providePlayerActivityPresenter(
        view: PlayerView,
        context: Context
    ): PlayerActivityPresenter {
        return PlayerActivityPresenter(
            playerView = view,
            context = context
        )
    }
}