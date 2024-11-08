package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository

class HistoryInteractorImpl(private val historyRepository: HistoryRepository): HistoryInteractor {
    override fun saveTracks(tracks: ArrayList<Track>) {
        historyRepository.saveTracks(tracks)
    }
    override fun loadTracks(): ArrayList<Track> {
        return historyRepository.loadTrack()
    }
    override fun clearHistory() {
        historyRepository.clearHistory()
    }
    override fun updateHistoryList(track: Track) {
        historyRepository.updateHistoryList(track)
    }
}