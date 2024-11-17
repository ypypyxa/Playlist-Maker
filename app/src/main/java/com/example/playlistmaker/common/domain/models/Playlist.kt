package com.example.playlistmaker.common.domain.models

import android.net.Uri
import java.io.Serializable

data class Playlist(
    val playlistId: Long,               // ID плейлиста
    val playlistName: String,           // Название плейлиста
    val playlistDescription: String,    // Описание плейлиста
    val artworkUri: Uri,                // Путь к файлу изображения для обложки
    val tracks: List<Track>,            // Список идентификаторов треков
    val tracksCount: Int                // Количество треков
) : Serializable
