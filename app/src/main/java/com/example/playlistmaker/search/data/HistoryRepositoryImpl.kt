package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.search.domain.api.HistoryRepository
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

    companion object {
        private const val HISTORY_TRACKS = "history_tracks"
        const val HISTORY = "history"
    }
}