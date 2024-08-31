package com.example.playlistmaker.media.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(private val playlists: String) : ViewModel() {

    private val playlistsLiveData = MutableLiveData(playlists)
    fun observePlaylists(): LiveData<String> = playlistsLiveData
}