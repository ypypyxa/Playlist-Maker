package com.example.playlistmaker.search.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchFragmentState
import com.example.playlistmaker.utils.SingleLiveEvent
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private var historyTracks = ArrayList<Track>()

    private var isSearchButtonPressed = false
    private var isRefreshButtonPressed = false

    private var latestSearchText: String? = null
    private var latestFragmentState: SearchFragmentState? = null

    private var buttonPressesDebounce: Job? = null

    private val searchLiveData = MutableLiveData<SearchFragmentState>()
    fun observeState(): LiveData<SearchFragmentState> = searchLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeToastState(): LiveData<String> = showToast

    private val showSearchEditClearButton = SingleLiveEvent<Boolean>()
    fun observeSearchEditClearButtonState(): LiveData<Boolean> = showSearchEditClearButton

    private val clearSearchEdit = SingleLiveEvent<String>()
    fun observeClearSearchEditCommand(): LiveData<String> = clearSearchEdit

    private val hideKeyboard = SingleLiveEvent<Boolean>()
    fun observeHideKeyboardCommand(): LiveData<Boolean> = hideKeyboard

    fun onResume() {
        if (latestFragmentState is SearchFragmentState.History) {
            historyTracks = historyInteractor.loadTracks()
            renderState(
                SearchFragmentState.History(historyTracks)
            )
        }
    }

    fun renderState(state: SearchFragmentState) {
        searchLiveData.postValue(state)
    }

    fun onFocusChange(hasFocus: Boolean, hasSearchTextIsEmpty: Boolean) {
        historyTracks = historyInteractor.loadTracks()
        HAS_FOCUS = hasFocus
        HAS_SEARCH_TEXT_IS_EMPTY = hasSearchTextIsEmpty
        if ( HAS_FOCUS && HAS_SEARCH_TEXT_IS_EMPTY && historyTracks.size != HISTORY_MIN_SIZE) {
            latestFragmentState = SearchFragmentState.History(historyTracks)
            renderState(
               SearchFragmentState.History(historyTracks)
            )
        }
    }

    fun onClearSearchButtonPress() {
        clearSearchEdit.postValue(EMPTY_TEXT)
        latestSearchText = EMPTY_TEXT
        historyTracks = historyInteractor.loadTracks()

        if (historyTracks.size > HISTORY_MIN_SIZE) {
            latestFragmentState = SearchFragmentState.History(historyTracks)
            renderState(
                SearchFragmentState.History(historyTracks)
            )
        } else {
            latestFragmentState = null
            renderState(
                SearchFragmentState.EmptyView
            )
        }
    }

    fun onClearHistoryButtonPress() {
        latestFragmentState = null
        renderState(
            SearchFragmentState.EmptyView
        )
        historyInteractor.clearTracks()
    }

    fun onSearchButtonPress() {
        isSearchButtonPressed = true
        hideKeyboard.postValue(true)
    }

    fun onRefreshButtonPress() {
        isRefreshButtonPressed = true
    }

    fun searchDebounce(searchText: String) {

        buttonPressesDebounce?.cancel()

        if (searchText.isEmpty()) {
            latestSearchText = EMPTY_TEXT
            if ( HAS_FOCUS && HAS_SEARCH_TEXT_IS_EMPTY && historyTracks.size != HISTORY_MIN_SIZE) {
                renderState(
                    SearchFragmentState.History(historyTracks)
                )
            } else {
                renderState(
                    SearchFragmentState.EmptyView
                )
            }
            isSearchButtonPressed = false
        } else {
            if (isSearchButtonPressed || isRefreshButtonPressed) {
                isSearchButtonPressed = false
                isRefreshButtonPressed = false
                if (latestSearchText != searchText) {
                    searchTrack(searchText)
                }
            }

            if (latestSearchText != searchText) {
                buttonPressesDebounce = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_DELAY)
                    searchTrack(searchText)
                }
            } else {
                return
            }
            showSearchEditClearButton.postValue(searchText.isNotEmpty())
        }
    }

    private fun searchTrack(searchText: String) {
        if (searchText.isNotEmpty()) {

            latestFragmentState = null
            latestSearchText = searchText

            renderState(
                SearchFragmentState.Loading
            )

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(searchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = ArrayList<Track>()

        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                showToast.postValue(errorMessage!!)
                when (errorMessage) {
                    getApplication<Application>().getString(R.string.nothing_found) -> {
                        latestFragmentState = null
                        renderState(
                            SearchFragmentState.EmptySearchResult(getApplication<Application>().getString(R.string.nothing_found))
                        )
                    }
                    else -> {
                        latestFragmentState = null
                        renderState(
                            SearchFragmentState.Error(getApplication<Application>().getString(R.string.something_went_wrong))
                        )
                    }
                }
            }
            tracks.isEmpty() -> {
                latestFragmentState = null
                renderState(
                    SearchFragmentState.EmptySearchResult(getApplication<Application>().getString(R.string.nothing_found))
                )
            }
            else -> {
                latestFragmentState = null
                renderState(
                    SearchFragmentState.SearchResult(tracks)
                )
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val HISTORY_MIN_SIZE = 0
        private const val EMPTY_TEXT = ""
        private var HAS_FOCUS = false
        private var HAS_SEARCH_TEXT_IS_EMPTY = true
    }
}