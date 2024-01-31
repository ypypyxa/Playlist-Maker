package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)

        fun bind(model: Track) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTime
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder_45x45)
                .fitCenter()
                .transform(RoundedCorners(2))
                .into(trackImage)
        }
}