package com.example.playlistmaker.media.favorites.data

import com.example.playlistmaker.root.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.favorites.data.db.FavoritesDatabase
import com.example.playlistmaker.root.data.db.entity.TrackEntity
import com.example.playlistmaker.media.favorites.domain.FavoritesRepository
import com.example.playlistmaker.root.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl (
    private val favoritesDatabase: FavoritesDatabase,
    private val trackDbConvertor: TrackDbConvertor,
    ) : FavoritesRepository {
        override fun favoritesTracks(): Flow<List<Track>> = flow {
            val tracks = favoritesDatabase.favoritesDao().getTracks()
            emit(convertFromTrackEntity(tracks))
        }

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