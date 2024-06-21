package com.example.playlistmaker.history.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.history.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl(context: Context) : HistoryRepository {

    private val historyGson = Gson()
    private val history = context.getSharedPreferences(HISTORY, MODE_PRIVATE)

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
        private const val HISTORY = "history"
    }
}