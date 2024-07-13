package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface HistoryRepository {
    fun saveTracks(trackList: ArrayList<Track>)
    fun loadTrack(): ArrayList<Track>
    fun clearHistory()
}