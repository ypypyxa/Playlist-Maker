package com.example.playlistmaker.common.domain.api

import com.example.playlistmaker.common.domain.models.Track

interface HistoryInteractor {
    fun saveTracks(tracks: ArrayList<Track>)
    fun loadTracks(): ArrayList<Track>
    fun clearHistory()
    fun updateHistoryList(track: Track)
}