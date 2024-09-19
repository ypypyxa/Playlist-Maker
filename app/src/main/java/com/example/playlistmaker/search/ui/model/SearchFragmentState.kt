package com.example.playlistmaker.search.ui.model

import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchFragmentState {

    // Состояние показа загрузки
    data object Loading : SearchFragmentState

    // Состояние пустого экрана
    data object EmptyView : SearchFragmentState

    // Состояние показа поиска
    data class SearchResult(
        val tracks: ArrayList<Track>
    ) : SearchFragmentState

    //Состояние показа пустого результата поиска
    data class EmptySearchResult(
        val message: String
    ) : SearchFragmentState

    // Состояние показа истории
    data class History (
        val tracks: ArrayList<Track>
    ) : SearchFragmentState

    // Состояние ошибки
    data class Error (
        val message: String
    ) : SearchFragmentState
}