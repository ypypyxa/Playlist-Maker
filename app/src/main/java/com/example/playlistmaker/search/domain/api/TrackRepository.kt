package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface TrackRepository {
    fun searchTrack(expression: String): List<Track>
}