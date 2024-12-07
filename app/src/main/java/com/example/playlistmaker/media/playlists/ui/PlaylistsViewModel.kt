package com.example.playlistmaker.media.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.playlists.ui.model.PlaylistsFragmentState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistsLiveData = MutableLiveData<PlaylistsFragmentState>()
    fun observePlaylistsState(): LiveData<PlaylistsFragmentState> = playlistsLiveData

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsFragmentState.Empty)
        } else {
            renderState(
                PlaylistsFragmentState.Content(
                playlists.sortedBy { it.playlistId }
            ))
        }
    }

    private fun renderState(state: PlaylistsFragmentState) {
        playlistsLiveData.postValue(state)
    }
}