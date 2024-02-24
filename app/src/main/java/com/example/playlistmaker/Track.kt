package com.example.playlistmaker

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
    ) : Serializable