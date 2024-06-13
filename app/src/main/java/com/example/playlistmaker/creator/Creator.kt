package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.presentation.PlayerActivityController
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.presentation.SearchActivityController
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.search.ui.TrackListAdapter

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

    fun provideMediaPlayer(): MediaPlayerInteractor {
        return MediaPlayerInteractor(getMediaPlayerRepository())
    }

    fun provideSearchActivityController(
        activity: SearchActivity,
        adapter: TrackListAdapter
    ): SearchActivityController {
        return SearchActivityController(
            searchActivity = activity,
            trackListAdapter = adapter
        ) }

    fun providePlayerActivityController(activity: PlayerActivity): PlayerActivityController {
        return PlayerActivityController(playerActivity = activity)
    }
}