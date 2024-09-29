package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.root.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
}