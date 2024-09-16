package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    fun addToFavorites(track: Track)
    fun removeFromFavorites(track: Track)
}