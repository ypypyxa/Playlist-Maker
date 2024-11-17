package com.example.playlistmaker.media.playlist.ui.model

sealed interface PlaylistFragmentState {

        object Content : PlaylistFragmentState

//        data class Content(
//            val tracks: List<Track>
//        ) : FavoritesFragmentState
}