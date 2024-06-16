package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.model.Track

interface SearchView {
    fun showSearchClearButton(isVisible: Boolean)
    fun showHistoryHint(isVisible: Boolean)
    fun showHistoryClearButton(isVisible: Boolean)
    fun showPlaceholderImage(isVisible: Boolean)
    fun setPlaceholderImage(drawable: Int)
    fun showPlaceholderMessage(isVisible: Boolean)
    fun setPlaceholderMessage (message: String)
    fun showPlaceholderButton(isVisible: Boolean)
    fun showProgressBar(isVisible: Boolean)
    fun showTrackListView(isVisible: Boolean)
    fun clearSearchEdit()
    fun updateTrackListView(tracks: List<Track>)

    fun hideKeyboard()
    fun showMessage(message: String)
}