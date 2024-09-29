package com.example.playlistmaker.root.data.converters

import com.example.playlistmaker.root.data.db.entity.TrackEntity
import com.example.playlistmaker.root.domain.model.Track
import com.example.playlistmaker.search.data.dto.TrackDto

class TrackDbConvertor {

    fun convert(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.inFavorite,
            track.addToFavoritesDate)
    }

    fun convert(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.inFavorite,
            track.addToFavoritesDate)
    }

    fun map(track: TrackDto): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.inFavorite,
            track.addToFavoritesDate
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.inFavorite,
            track.addToFavoritesDate
        )
    }
}