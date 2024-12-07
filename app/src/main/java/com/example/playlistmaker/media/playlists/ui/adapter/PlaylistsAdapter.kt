package com.example.playlistmaker.media.playlists.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist

class PlaylistsAdapter(private val onItemClickListener: (Playlist) -> Unit) : RecyclerView.Adapter<PlaylistsViewHolder>() {

        var playlists: MutableList<Playlist> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
            return PlaylistsViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
            holder.bind(playlists[position])
            holder.itemView.setOnClickListener { onItemClickListener(playlists[holder.adapterPosition]) }
        }

        override fun getItemCount(): Int {
            return playlists.size
        }
}