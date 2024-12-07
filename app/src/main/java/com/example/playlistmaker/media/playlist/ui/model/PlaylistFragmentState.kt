package com.example.playlistmaker.media.playlist.ui.model

import com.example.playlistmaker.common.domain.models.Playlist

sealed interface PlaylistFragmentState {

        object Content : PlaylistFragmentState

        data class RefreshContent(val playlist: Playlist) : PlaylistFragmentState
}