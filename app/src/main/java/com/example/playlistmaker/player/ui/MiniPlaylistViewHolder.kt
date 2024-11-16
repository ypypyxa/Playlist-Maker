package com.example.playlistmaker.player.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist

class MiniPlaylistViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val playlistCover: ImageView = view.findViewById(R.id.playlistImageMini)
    private val playlistName: TextView = view.findViewById(R.id.playlistNameMini)
    private val tracksCount: TextView = view.findViewById(R.id.tracksCount)

    fun bind(model: Playlist) {
        playlistName.text = model.playlistName
        tracksCount.text = getTrackWord(model.tracksCount)
        Glide.with(itemView)
            .load(model.artworkUri)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(itemView.context)))
            .into(playlistCover)
    }
    private fun dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            CORNERS_ANGLE,
            context.resources.displayMetrics).toInt()
    }

    private fun getTrackWord(count: Int): String {
        val remainder10 = count % 10
        val remainder100 = count % 100

        return when {
            remainder100 in 11..19 -> "$count треков" // Исключение для чисел от 11 до 19
            remainder10 == 1 -> "$count трек" // Например, 1, 21, 31
            remainder10 in 2..4 -> "$count трека" // Например, 2, 3, 4, 22, 23, 24
            else -> "$count треков" // Все остальные случаи
        }
    }

    companion object{
        private const val CORNERS_ANGLE = 8.0F
    }
}