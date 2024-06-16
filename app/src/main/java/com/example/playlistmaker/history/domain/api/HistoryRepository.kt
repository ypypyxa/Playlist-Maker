package com.example.playlistmaker.history.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface HistoryRepository {
    fun saveTracks(trackList: ArrayList<Track>)
    fun loadTrack(): ArrayList<Track>
    fun clearTrack()
}