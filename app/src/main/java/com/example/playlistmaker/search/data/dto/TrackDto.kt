package com.example.playlistmaker.search.data.dto

data class TrackDto(
    val trackId: String, // Идентификатор трека
    val trackName: String, // Название трека
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String, // Год релиза
    val primaryGenreName: String, // Жанр
    val country: String, // Страна исполнителя
    val previewUrl: String // Адрес файла трека
)