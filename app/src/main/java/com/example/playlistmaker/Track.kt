package com.example.playlistmaker

import java.io.Serializable

data class Track(
    val trackId: String, //Идентификатор трека
    val trackName: String, // Название трека
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
    ) : Serializable