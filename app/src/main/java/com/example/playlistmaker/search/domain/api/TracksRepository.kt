package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun addToFavorites(track: Track)
    fun removeFromFavorites(track: Track)
}