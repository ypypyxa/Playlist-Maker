package com.example.playlistmaker.common.data

import android.content.SharedPreferences
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.domain.api.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl(val history: SharedPreferences) : HistoryRepository {

    private val historyGson = Gson()

// Функция сохранения trackList в SharedPreferences/history
    override fun saveTracks(trackList: ArrayList<Track>) {
        history.edit()
            .putString(HISTORY_TRACKS, historyGson.toJson(trackList))
            .apply()
    }

// Функция для загрузки trackList из SharedPreferences/history
    override fun loadTrack(): ArrayList<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return historyGson.fromJson(history.getString(HISTORY_TRACKS, ""), type) ?: ArrayList()
    }

    override fun clearHistory() {
        history.edit().remove(HISTORY_TRACKS).apply()
    }

    override fun updateHistoryList(track: Track) {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        val historyTracks: ArrayList<Track> = historyGson.fromJson(history.getString(HISTORY_TRACKS, ""), type) ?: ArrayList()

        var index: Int? = null

        if (historyTracks.isEmpty()) {
            historyTracks.add(0, track)
        } else {
            for (i in 0 .. historyTracks.size-1) {
                if (historyTracks[i].trackId == track.trackId) {
                    index = i
                }
            }
            if (index != null) { historyTracks.remove(historyTracks[index]) }
            historyTracks.add(0, track)
        }
        if (historyTracks.size > HISTORY_MAX_SIZE) {
            historyTracks.removeAt(HISTORY_MAX_SIZE)
        }
        history.edit()
            .putString(HISTORY_TRACKS, historyGson.toJson(historyTracks))
            .apply()
    }

    companion object {
        private const val HISTORY_TRACKS = "history_tracks"
        const val HISTORY = "history"
        private const val HISTORY_MAX_SIZE = 10
    }
}