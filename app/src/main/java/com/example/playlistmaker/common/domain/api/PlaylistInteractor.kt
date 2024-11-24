package com.example.playlistmaker.common.domain.api

import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
    suspend fun deleteTrackFromPlaylist(playlistId: Long, track: Track)

    suspend fun updatePlaylist(playlist: Playlist)
}