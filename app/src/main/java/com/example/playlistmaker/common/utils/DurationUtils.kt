package com.example.playlistmaker.common.utils

import com.example.playlistmaker.common.domain.models.Track

object DurationUtils {

    fun getTotalMinutes(tracks: List<Track>): String {
        val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
        val totalMinutes = totalDurationMillis / (1000 * 60)
        val minutesString = getMinuteWord(totalMinutes.toInt())
        return if (totalMinutes > 0) {
            "$totalMinutes $minutesString"
        } else if (totalMinutes == 0L) {
            "0 минут"
        } else {
            "Менее минуты"
        }
    }

    private fun getMinuteWord(count: Int): String {
        val remainder10 = count % 10
        val remainder100 = count % 100

        return when {
            remainder100 in 11..14 -> "минут"
            remainder10 == 1 -> "минута"
            remainder10 in 2..4 -> "минуты"
            else -> "минут"
        }
    }
}