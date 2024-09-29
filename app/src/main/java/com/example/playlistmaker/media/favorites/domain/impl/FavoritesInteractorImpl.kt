package com.example.playlistmaker.media.favorites.domain.impl

import com.example.playlistmaker.media.favorites.domain.FavoritesRepository
import com.example.playlistmaker.media.favorites.domain.db.FavoritesInteractor
import com.example.playlistmaker.root.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor {
    override fun favoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.favoritesTracks()
    }

    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        favoritesRepository.removeFromFavorites(track)
    }
}