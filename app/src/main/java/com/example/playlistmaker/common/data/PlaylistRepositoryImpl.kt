package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.converters.PlaylistDbConverter
import com.example.playlistmaker.common.data.db.database.PlaylistsDatabase
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.domain.api.PlaylistsRepository
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

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        // Получаем текущий плейлист из базы данных
        val playlistEntity = playlistsDatabase.playlistDao().getPlaylistById(playlistId)
        val playlist = playlistDbConverter.convert(playlistEntity)

        // Добавляем новый трек в список треков
        val updatedTracks = playlist.tracks.toMutableList().apply { add(track) }
        val updatedTracksCount = updatedTracks.size

        // Создаем обновленный плейлист
        val updatedPlaylist = playlist.copy(
            tracks = updatedTracks,
            tracksCount = updatedTracksCount
        )

        // Конвертируем обратно в сущность и обновляем плейлист в базе данных
        val updatedPlaylistEntity = playlistDbConverter.convert(updatedPlaylist)
        playlistsDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)
    }
}