package com.example.playlistmaker.media.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.playlists.ui.model.PlaylistFragmentState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistsLiveData = MutableLiveData<PlaylistFragmentState>()
    fun observePlaylistsState(): LiveData<PlaylistFragmentState> = playlistsLiveData

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
            renderState(PlaylistFragmentState.Empty)
        } else {
            renderState(
                PlaylistFragmentState.Content(
                playlists.sortedBy { it.playlistId }
            ))
        }
    }

    private fun renderState(state: PlaylistFragmentState) {
        playlistsLiveData.postValue(state)
    }
}