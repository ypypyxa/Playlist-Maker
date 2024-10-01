package com.example.playlistmaker.common.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long,              // Идентификатор трека
    val trackName: String,          // Название трека
    val artistName: String,         // Имя исполнителя
    val trackTimeMillis: String,    // Продолжительность трека
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val collectionName: String,     // Название альбома
    val releaseDate: String,        // Год релиза
    val primaryGenreName: String,   // Жанр
    val country: String,            // Страна исполнителя
    val previewUrl: String,         // Адрес файла трека
    val inFavorite: Boolean,        // Добавлен ли трек в избранное
    val addToFavoritesDate: Long    // Дата добавления трека в избранное
    ) : Serializable