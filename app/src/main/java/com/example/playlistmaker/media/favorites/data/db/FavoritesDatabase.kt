package com.example.playlistmaker.media.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.common.data.db.dao.TracksDao
import com.example.playlistmaker.common.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class FavoritesDatabase : RoomDatabase(){

    abstract fun favoritesDao(): TracksDao
}