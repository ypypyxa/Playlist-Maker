package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.player.utils.InFavoriteSettings
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.util.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val inFavoriteSettings: InFavoriteSettings
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                val favorites = inFavoriteSettings.getSavedFavorites()

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
                        inFavorite = favorites.contains(it.trackId)
                    )
                } )
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun addToFavorites(track: Track) {
        inFavoriteSettings.addToFavorites(track.trackId)
    }

    override fun removeFromFavorites(track: Track) {
        inFavoriteSettings.removeFromFavorites(track.trackId)
    }
}