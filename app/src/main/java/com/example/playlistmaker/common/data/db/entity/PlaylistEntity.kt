package com.example.playlistmaker.common.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")                  // Идентификатор плейлиста
    val playlistId: Long,
    @ColumnInfo(name = "playlist_name")                // Название плейлиста
    val playlistName: String,
    @ColumnInfo(name = "playlist_description")         // Описание плейлиста
    val playlistDescription: String,
    @ColumnInfo(name = "uri")                          // Путь к файлу изображения для обложки
    val artworkUri: String,
    @ColumnInfo(name = "tracks")                       // Список идентификаторов треков
    val tracks: String,
    @ColumnInfo(name = "tracks_count")                 // Количество треков
    val tracksCount: Int
)