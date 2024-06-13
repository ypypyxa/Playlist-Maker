package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.util.Resource

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}