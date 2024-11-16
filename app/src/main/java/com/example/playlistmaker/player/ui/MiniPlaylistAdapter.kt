package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist

class MiniPlaylistAdapter(private val onItemClickListener: (Playlist) -> Unit) : RecyclerView.Adapter<MiniPlaylistViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_mini_item, parent, false)
        return MiniPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiniPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onItemClickListener(playlists[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}