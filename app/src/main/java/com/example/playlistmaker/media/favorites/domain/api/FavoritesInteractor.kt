package com.example.playlistmaker.media.favorites.domain.api

import com.example.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun favoritesTracks(): Flow<List<Track>>

    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
}