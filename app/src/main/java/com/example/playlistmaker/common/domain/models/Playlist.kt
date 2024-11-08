package com.example.playlistmaker.common.domain.models

data class Playlist(
    val playlistId: Long,               // ID плейлиста
    val playlistName: String,           // Название плейлиста
    val playlistDescription: String,    // Описание плейлиста
    val artworkUri: String,             // Путь к файлу изображения для обложки
    val tracks: String,                 // Список идентификаторов треков
    val tracksCount: Int                // Количество треков
)
