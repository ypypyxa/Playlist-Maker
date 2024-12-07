package com.example.playlistmaker.media.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.ui.adapters.TrackListViewHolder

class TracksOnPlaylistAdapter (
    private val onItemClickListener: (Track) -> Unit,
    private val onItemLongClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackListViewHolder>() {

    var trackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClickListener(track)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(track)
            true // Возвращаем true, чтобы указать, что событие обработано
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}