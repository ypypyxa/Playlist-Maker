package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryManager(private val history : SharedPreferences) {

    private val historyGson = Gson()

//Функция сохранения trackList в SharedPreferences/history
    fun saveTrackList(trackList: ArrayList<Track>) {

        history.edit()
            .putString(HISTORY_TRACKS, historyGson.toJson(trackList))
            .apply()
    }

    // Функция для загрузки trackList из SharedPreferences/history
    fun loadTrackList(): ArrayList<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return historyGson.fromJson(history.getString(HISTORY_TRACKS, ""), type) ?: ArrayList()
    }

    fun clearTrackList() {
        history.edit().remove(HISTORY_TRACKS).apply()
    }

    companion object {
        private const val HISTORY_TRACKS = "history_tracks"
    }
}