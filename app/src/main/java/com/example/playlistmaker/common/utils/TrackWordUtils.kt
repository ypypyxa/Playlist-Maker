package com.example.playlistmaker.common.utils

import android.content.Context
import com.example.playlistmaker.R

class TrackWordUtils(private val context: Context) {

    fun getTrackWord(count: Int): String {
        val remainder10 = count % 10
        val remainder100 = count % 100

        return when {
            remainder100 in 11..19 -> "$count ${context.getString(R.string.tracks)}" // Исключение для чисел от 11 до 19
            remainder10 == 1 -> "$count ${context.getString(R.string.track)}" // Например, 1, 21, 31
            remainder10 in 2..4 -> "$count ${context.getString(R.string.alt_tracks)}" // Например, 2, 3, 4, 22, 23, 24
            else -> "$count ${context.getString(R.string.tracks)}" // Все остальные случаи
        }
    }
}