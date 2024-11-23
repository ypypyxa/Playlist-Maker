package com.example.playlistmaker.media.playlist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.media.playlist.ui.model.PlaylistFragmentState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<PlaylistFragmentState>()
    fun observeState(): LiveData<PlaylistFragmentState> = stateLiveData

    private fun renderState(state: PlaylistFragmentState) {
        stateLiveData.postValue(state)
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
}