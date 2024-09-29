package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.root.domain.model.Track

class TrackListAdapter (private val onItemClickListener: (Track) -> Unit) : RecyclerView.Adapter<TrackListViewHolder> () {

    var trackList: MutableList<Track> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { onItemClickListener(trackList[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}