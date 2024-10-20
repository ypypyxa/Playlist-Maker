package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.common.domain.models.Track

interface HistoryRepository {
    fun saveTracks(trackList: ArrayList<Track>)
    fun loadTrack(): ArrayList<Track>
    fun clearHistory()
    fun updateHistoryList(track: Track)
}