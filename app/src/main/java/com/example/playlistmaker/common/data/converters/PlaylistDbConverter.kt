package com.example.playlistmaker.common.data.converters

import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.domain.models.Playlist

class PlaylistDbConverter {
    fun convert(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.artworkUri,
            playlist.tracks,
            playlist.tracksCount
        )
    }

    fun convert(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.artworkUri,
            playlist.tracks,
            playlist.tracksCount
        )
    }
}