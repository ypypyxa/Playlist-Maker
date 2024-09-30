package com.example.playlistmaker.media.favorites.ui.models

import com.example.playlistmaker.common.domain.models.Track

sealed interface FavoritesFragmentState {

    object Empty : FavoritesFragmentState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesFragmentState
}