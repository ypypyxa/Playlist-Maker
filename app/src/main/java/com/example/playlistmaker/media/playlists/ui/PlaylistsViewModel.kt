package com.example.playlistmaker.media.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.playlists.ui.model.PlaylistFragmentState

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistsLiveData = MutableLiveData<PlaylistFragmentState>()
    fun observePlaylists(): LiveData<PlaylistFragmentState> = playlistsLiveData


}