package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as? TrackSearchResponse)?.results?.map {
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
            } ?: emptyList()
        } else {
            return emptyList()
        }
    }
}