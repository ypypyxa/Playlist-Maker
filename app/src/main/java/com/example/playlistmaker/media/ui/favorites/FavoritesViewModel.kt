package com.example.playlistmaker.media.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel(private val favorites: String) : ViewModel() {

    private val favoritesLiveData = MutableLiveData(favorites)
    fun observeFavorites(): LiveData<String> = favoritesLiveData
}