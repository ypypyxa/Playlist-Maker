package com.example.playlistmaker.media.favorites.data

import com.example.playlistmaker.common.data.converters.TrackDbConvertor
import com.example.playlistmaker.common.data.db.database.FavoritesDatabase
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.media.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl (
    private val favoritesDatabase: FavoritesDatabase,
    private val trackDbConvertor: TrackDbConvertor,
    ) : FavoritesRepository {
        override fun favoritesTracks(): Flow<List<Track>> =
            favoritesDatabase.favoritesDao().getTracks()
                .map { entites ->
                    entites.map { entity -> trackDbConvertor.map(entity) }
                }
                .flowOn(Dispatchers.IO)

    override suspend fun addToFavorites(track: Track) {
        favoritesDatabase.favoritesDao().insertNewTrack(trackDbConvertor.convert(track))
    }

    override suspend fun removeFromFavorites(track: Track) {
        favoritesDatabase.favoritesDao().deleteTrackEntity(trackDbConvertor.convert(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}