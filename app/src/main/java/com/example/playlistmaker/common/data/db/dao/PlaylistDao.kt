package com.example.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    // Чтение
    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>
    @Query("SELECT * FROM playlist_table WHERE playlist_id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity
    @Query("SELECT * FROM playlist_table WHERE playlist_name LIKE :search")
    suspend fun searchPlaylistByName(search: String): List<PlaylistEntity>

    // Cохранение
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPlaylist(playlistEntity: PlaylistEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    // Удаление
    @Delete()
    suspend fun deletePlaylistEntity(playlistEntity: PlaylistEntity)
}