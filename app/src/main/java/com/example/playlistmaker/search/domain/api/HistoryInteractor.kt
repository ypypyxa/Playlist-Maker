package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface HistoryInteractor {
    fun saveTracks(tracks: ArrayList<Track>)
    fun loadTracks(): ArrayList<Track>
    fun clearTracks()
}