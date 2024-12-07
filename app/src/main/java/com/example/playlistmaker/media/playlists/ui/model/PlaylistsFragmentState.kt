package com.example.playlistmaker.media.playlists.ui.model

import com.example.playlistmaker.common.domain.models.Playlist

sealed interface PlaylistsFragmentState {

    object Empty : PlaylistsFragmentState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsFragmentState
}