package com.example.playlistmaker.media.favorites.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.media.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.media.favorites.ui.models.FavoritesFragmentState
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesFragmentState>()
    fun observeState(): LiveData<FavoritesFragmentState> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor
                .favoritesTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesFragmentState.Empty)
        } else {
            renderState(FavoritesFragmentState.Content(
                tracks.sortedByDescending { it.addToFavoritesDate }
            ))
        }
    }

    private fun renderState(state: FavoritesFragmentState) {
        stateLiveData.postValue(state)
    }
}