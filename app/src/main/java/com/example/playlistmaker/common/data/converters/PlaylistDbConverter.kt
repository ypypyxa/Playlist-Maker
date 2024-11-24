package com.example.playlistmaker.common.data.converters

import androidx.core.net.toUri
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {

    private val tracksGson = Gson()

    fun convert(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Track>>() {}.type
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.artworkUri,
            tracksGson.fromJson(playlistEntity.tracks, type),
            playlistEntity.tracksCount
        )
    }

    fun convert(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.artworkUri,
            tracksGson.toJson(playlist.tracks),
            playlist.tracksCount
        )
    }
}