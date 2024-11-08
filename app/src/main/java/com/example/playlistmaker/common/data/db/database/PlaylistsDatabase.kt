package com.example.playlistmaker.common.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.common.data.db.dao.PlaylistDao
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity

@Database(version = 1, entities = [PlaylistEntity::class])
abstract class PlaylistsDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
}