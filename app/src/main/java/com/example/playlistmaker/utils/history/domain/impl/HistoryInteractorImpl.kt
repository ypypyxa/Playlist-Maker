package com.example.playlistmaker.utils.history.domain.impl

import com.example.playlistmaker.utils.history.domain.api.HistoryInteractor
import com.example.playlistmaker.utils.history.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.model.Track

class HistoryInteractorImpl(private val historyRepository: HistoryRepository): HistoryInteractor {
    override fun saveTracks(tracks: ArrayList<Track>) {
        historyRepository.saveTracks(tracks)
    }
    override fun loadTracks(): ArrayList<Track> {
        return historyRepository.loadTrack()
    }
    override fun clearTracks() {
        historyRepository.clearHistory()
    }
}