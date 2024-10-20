package com.example.playlistmaker.media.playlists.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.playlists.ui.model.CreatePlaylistState

class CreatePlaylistViewModel : ViewModel() {

    private val createPlaylistLiveData = MutableLiveData<CreatePlaylistState>()

    private val _isCreateButtonEnabled = MutableLiveData(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    fun setPlaylistName(name: String) {
        _isCreateButtonEnabled.value = name.isNotBlank()
        createPlaylistLiveData.value?.name = name
    }
    fun setPlaylistDescription(description: String) {
        createPlaylistLiveData.value?.descripton = description
    }
    fun setPlaylistUri(uri: Uri) {
        createPlaylistLiveData.value?.uri = uri
    }

    fun isEdited(): Boolean {
        return !((createPlaylistLiveData.value?.name?.isEmpty() == false)
               &&(createPlaylistLiveData.value?.descripton?.isEmpty() == false)
               &&(createPlaylistLiveData.value?.uri?.toString()?.isEmpty() == false))
    }
}