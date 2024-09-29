package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.R
import com.example.playlistmaker.media.favorites.data.db.FavoritesDatabase
import com.example.playlistmaker.root.data.converters.TrackDbConvertor
import com.example.playlistmaker.root.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favorites: FavoritesDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            404 -> {
                emit(Resource.Error((R.string.nothing_found).toString()))            }
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val favoriteTracks = loadFavorites()

                emit(
                    Resource.Success(
                        (response as TracksSearchResponse).results.map {
                            val trackId = it.trackId.toLong()
                            val trackName = it.trackName ?: ""
                            val artistName = it.artistName ?: ""
                            val trackTimeMillis = it.trackTimeMillis ?: ""
                            val artworkUrl100 = it.artworkUrl100 ?: ""
                            val collectionName = it.collectionName ?: ""
                            val releaseDate = it.releaseDate ?: ""
                            val primaryGenreName = it.primaryGenreName ?: ""
                            val country = it.country ?: ""
                            val previewUrl = it.previewUrl ?: ""
                            val inFavorite = favoriteTracks.any { favoriteTrack -> favoriteTrack.trackId == trackId }
                            val addToFavoritesDate = favoriteTracks.firstOrNull { favoriteTrack -> favoriteTrack.trackId == trackId }?.addToFavoritesDate ?: 0
                            Track(
                                trackId,
                                trackName,
                                artistName,
                                trackTimeMillis,
                                artworkUrl100,
                                collectionName,
                                releaseDate,
                                primaryGenreName,
                                country,
                                previewUrl,
                                inFavorite,
                                addToFavoritesDate
                            )
                        }
                    )
                )
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    private suspend fun loadFavorites(): List<Track> = withContext(Dispatchers.IO) {
        favorites.favoritesDao().getTracks().map { track -> trackDbConvertor.map(track) }
    }
}