package com.example.playlistmaker.media.playlists.domain.impl

import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.media.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.playlists.domain.api.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }
    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }
}