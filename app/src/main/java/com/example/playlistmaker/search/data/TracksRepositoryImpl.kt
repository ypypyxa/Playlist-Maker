package com.example.playlistmaker.search.data

import com.example.playlistmaker.player.data.Favorites
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favorites: Favorites
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                val favoriteTracks = favorites.getSavedFavorites()

                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                        inFavorite = favoriteTracks.contains(it.trackId)
                    )
                } )
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun addToFavorites(track: Track) {
        favorites.addToFavorites(track.trackId)
    }

    override fun removeFromFavorites(track: Track) {
        favorites.removeFromFavorites(track.trackId)
    }
}