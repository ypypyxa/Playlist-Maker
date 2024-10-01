package com.example.playlistmaker.common.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class TrackEntity(
    @PrimaryKey @ColumnInfo(name = "track_id")      // Идентификатор трека
    val trackId: Long,
    @ColumnInfo(name = "track_name")                // Название трека
    val trackName: String,
    @ColumnInfo(name = "artist_name")               // Имя исполнителя
    val artistName: String,
    @ColumnInfo(name = "track_time")                // Продолжительность трека
    val trackTimeMillis: String,
    @ColumnInfo(name = "artwork_url100")            // Ссылка на изображение обложки
    val artworkUrl100: String,
    @ColumnInfo(name = "collection")                // Название альбома
    val collectionName: String,
    @ColumnInfo(name = "release_date")              // Год релиза
    val releaseDate: String,
    @ColumnInfo(name = "genre")                     // Жанр
    val primaryGenreName: String,
    @ColumnInfo(name = "country")                   // Страна исполнителя
    val country: String,
    @ColumnInfo(name = "preview_url")               // Адрес файла трека
    val previewUrl: String,
    @ColumnInfo(name = "favorite")                  // Добавлен ли трек в избранное
    val inFavorite: Boolean,
    @ColumnInfo(name = "add_date")                  // Дата добавления трека в избранное
    val addToFavoritesDate: Long
)