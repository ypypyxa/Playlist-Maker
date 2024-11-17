package com.example.playlistmaker.media.playlist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.playlist.ui.model.PlaylistFragmentState
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<PlaylistFragmentState>()
    fun observeState(): LiveData<PlaylistFragmentState> = stateLiveData

    private fun renderState(state: PlaylistFragmentState) {
        stateLiveData.postValue(state)
    }

    fun fillData() {
        renderState(PlaylistFragmentState.Content)
    }
}