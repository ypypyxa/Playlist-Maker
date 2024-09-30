package com.example.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {

    // Чтение
    @Query("SELECT * FROM favorites_table")
    fun getTracks(): Flow<List<TrackEntity>>
    @Query("SELECT * FROM favorites_table WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity
    @Query("SELECT * FROM favorites_table WHERE track_name LIKE :search")
    suspend fun searchTrackByName(search: String): List<TrackEntity>

    // Cохранение
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTrack(trackEntity: TrackEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTracks(trackEntity: TrackEntity)

    // Удаление
    @Delete()
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)
}