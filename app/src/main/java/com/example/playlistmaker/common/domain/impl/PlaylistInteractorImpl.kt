package com.example.playlistmaker.common.domain.impl

import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.api.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }
    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        playlistRepository.addTrackToPlaylist(playlistId, track)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, track: Track) {
        playlistRepository.deleteTrackFromPlaylist(playlistId, track)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }
}