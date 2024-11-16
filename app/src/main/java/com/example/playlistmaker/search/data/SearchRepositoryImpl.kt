package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.R
import com.example.playlistmaker.common.data.db.database.FavoritesDatabase
import com.example.playlistmaker.common.data.converters.TrackDbConvertor
import com.example.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favorites: FavoritesDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            404 -> {
                emit(Resource.Error((R.string.nothing_found).toString()))            }
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                // Получаем избранные треки из DAO как Flow
                val favoriteTracksFlow = favorites.favoritesDao().getTracks()
                    .map { entities -> entities.map { trackDbConvertor.map(it) } }
                // Собираем список избранных треков
                val favoriteTracks = favoriteTracksFlow.first()

                emit(
                    Resource.Success(
                        (response as TracksSearchResponse).results.map {
                            val trackId = it.trackId
                            val trackName = it.trackName
                            val artistName = it.artistName
                            val trackTimeMillis = it.trackTimeMillis
                            val artworkUrl100 = it.artworkUrl100
                            val collectionName = it.collectionName
                            val releaseDate = it.releaseDate
                            val primaryGenreName = it.primaryGenreName
                            val country = it.country
                            val previewUrl = it.previewUrl
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
}