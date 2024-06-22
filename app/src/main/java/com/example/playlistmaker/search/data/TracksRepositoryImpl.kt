package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    val trackId = it.trackId ?: ""
                    val trackName = it.trackName ?: ""
                    val artistName = it.artistName ?: ""
                    val trackTimeMillis = it.trackTimeMillis ?: ""
                    val artworkUrl100 = it.artworkUrl100 ?: ""
                    val collectionName = it.collectionName ?: ""
                    val releaseDate = it.releaseDate ?: ""
                    val primaryGenreName = it.primaryGenreName ?: ""
                    val country = it.country ?: ""
                    val previewUrl = it.previewUrl ?: ""
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
                        previewUrl
                    )
                } ?: emptyList() )
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}