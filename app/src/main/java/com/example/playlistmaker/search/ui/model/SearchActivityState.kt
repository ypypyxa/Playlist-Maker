package com.example.playlistmaker.search.ui.model

import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchActivityState {

    // Состояние показа загрузки
    data object Loading : SearchActivityState

    // Состояние пустого экрана
    data object EmptyView : SearchActivityState

    // Состояние показа поиска
    data class SearchResult(
        val tracks: ArrayList<Track>
    ) : SearchActivityState

    //Состояние показа пустого результата поиска
    data class EmptySearchResult(
        val message: String
    ) : SearchActivityState

    // Состояние показа истории
    data class History (
        val tracks: ArrayList<Track>
    ) : SearchActivityState

    // Состояние ошибки
    data class Error (
        val message: String
    ) : SearchActivityState
}