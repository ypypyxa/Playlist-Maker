package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.ui.model.SearchActivityState

interface SearchView {

    fun render(state: SearchActivityState)

// Одноразовые события
    fun clearSearchEdit()
    fun showSearchEditClearButton(isVisible: Boolean)
    fun hideKeyboard()
    fun showToast(message: String)
}