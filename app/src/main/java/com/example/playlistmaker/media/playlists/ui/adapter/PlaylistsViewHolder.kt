package com.example.playlistmaker.media.playlists.ui.adapter

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
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.utils.TrackWordUtils

class PlaylistsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val context = itemView.context

    private val playlistCover: ImageView = view.findViewById(R.id.playlistImage)
    private val playlistName: TextView = view.findViewById(R.id.playlistName)
    private val tracksCount: TextView = view.findViewById(R.id.tracksCount)

    fun bind(model: Playlist) {

        playlistName.text = model.playlistName
        tracksCount.text = TrackWordUtils(context).getTrackWord(model.tracksCount)
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

    companion object{
        private const val CORNERS_ANGLE = 8.0F
    }
}