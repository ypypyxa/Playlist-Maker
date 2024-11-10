package com.example.playlistmaker.media.playlists.data

import com.example.playlistmaker.common.data.converters.PlaylistDbConverter
import com.example.playlistmaker.common.data.db.database.PlaylistsDatabase
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.media.playlists.domain.api.PlaylistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistsDatabase: PlaylistsDatabase,
    private val playlistDbConverter: PlaylistDbConverter) : PlaylistsRepository {

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistsDatabase.playlistDao().getPlaylists()
            .map { entites ->
                entites.map { entity -> playlistDbConverter.convert(entity) }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsDatabase.playlistDao().insertNewPlaylist(playlistDbConverter.convert(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsDatabase.playlistDao().deletePlaylistEntity(playlistDbConverter.convert(playlist))
    }
}