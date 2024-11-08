package com.example.playlistmaker.media.playlists.ui.model

import com.example.playlistmaker.common.domain.models.Playlist

sealed interface PlaylistFragmentState {

    object Empty : PlaylistFragmentState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistFragmentState
}