package com.example.playlistmaker.media.playlist.ui

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.utils.TrackWordUtils
import com.example.playlistmaker.media.playlist.ui.model.PlaylistFragmentState
import com.example.playlistmaker.settings.domain.api.SharingInteractor
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<PlaylistFragmentState>()
    fun observeState(): LiveData<PlaylistFragmentState> = stateLiveData

    private fun renderState(state: PlaylistFragmentState) {
        stateLiveData.postValue(state)
    }

    fun buildShareText(playlist: Playlist): String {
        val builder = StringBuilder()

        // Добавляем название плейлиста
        builder.append(playlist.playlistName).append("\n")

        // Добавляем описание плейлиста, если есть
        if (playlist.playlistDescription.isNotEmpty()) {
            builder.append(playlist.playlistDescription).append("\n")
        }

        // Добавляем количество треков
        val tracksCountText = "${playlist.tracksCount} ${
            TrackWordUtils(application).getTrackWord(playlist.tracksCount)
        }"
        builder.append(tracksCountText).append("\n\n")

        // Формируем список треков
        playlist.tracks.forEachIndexed { index, track ->
            val trackDuration = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                .format(track.trackTimeMillis)
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName} ($trackDuration)")
                .append("\n")
        }

        return builder.toString()
    }

    fun fillData() {
        renderState(PlaylistFragmentState.Content)
    }

    fun deleteTrack(playlistId: Long, track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, track)
            // После удаления трека, обновляем плейлист
            refreshPlaylist(playlistId)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)

            // Удаляем файл обложки плейлиста, если он есть
            val artworkUri = playlist.artworkUri.toUri()
            if (artworkUri != null && artworkUri.scheme == "file") {
                val file = File(artworkUri.path!!)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (!deleted) {
                        // Логируем или обрабатываем ситуацию, если файл не удалось удалить
                        Log.e("PlaylistViewModel", "Failed to delete file: ${file.absolutePath}")
                    }
                }
            }
        }
    }

    private suspend fun refreshPlaylist(playlistId: Long) {
        // Получаем обновленный плейлист из базы данных
        val updatedPlaylist = playlistInteractor.getPlaylists()
            .firstOrNull()
            ?.find { it.playlistId == playlistId }

        if (updatedPlaylist != null) {
            val playlist = updatedPlaylist
            // Обновляем состояние
            renderState(PlaylistFragmentState.RefreshContent(playlist))
        }
    }

    fun sharePlaylist(playlist: String) {
        sharingInteractor.sharePlaylist(playlist)
    }

    companion object {
        private const val TIME_FORMAT = "m:ss"
    }
}