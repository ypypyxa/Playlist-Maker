package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchActivityState
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private var historyTracks = ArrayList<Track>()

    private val handler = Handler(Looper.getMainLooper())
    private var isSearchButtonPressed = false
    private var isRefreshButtonPressed = false

    private var latestSearchText: String? = null

    private val searchActivityLiveData = MutableLiveData<SearchActivityState>()
    fun observeState(): LiveData<SearchActivityState> = searchActivityLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeToastState(): LiveData<String> = showToast

    private val showSearchEditClearButton = SingleLiveEvent<Boolean>()
    fun observeSearchEditClearButtonState(): LiveData<Boolean> = showSearchEditClearButton

    private val clearSearchEdit = SingleLiveEvent<String>()
    fun observeClearSearchEditCommand(): LiveData<String> = clearSearchEdit

    private val hideKeyboard = SingleLiveEvent<Boolean>()
    fun observeHideKeyboardCommand(): LiveData<Boolean> = hideKeyboard

    fun onDestroy() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun renderState(state: SearchActivityState) {
        searchActivityLiveData.postValue(state)
    }

    fun onFocusChange(hasFocus: Boolean, hasSearchTextIsEmpty: Boolean) {
        historyTracks = historyInteractor.loadTracks()
        HAS_FOCUS = hasFocus
        HAS_SEARCH_TEXT_IS_EMPTY = hasSearchTextIsEmpty
        if ( HAS_FOCUS && HAS_SEARCH_TEXT_IS_EMPTY && historyTracks.size != HISTORY_MIN_SIZE) {
            renderState(
               SearchActivityState.History(historyTracks)
            )
        }
    }

    fun onClearSearchButtonPress() {
        clearSearchEdit.postValue(EMPTY_TEXT)
        latestSearchText = EMPTY_TEXT
        historyTracks = historyInteractor.loadTracks()

        if (historyTracks.size > HISTORY_MIN_SIZE) {
            renderState(
                SearchActivityState.History(historyTracks)
            )
        } else {
            renderState(
                SearchActivityState.EmptyView
            )
        }
    }

    fun onClearHistoryButtonPress() {
        renderState(
            SearchActivityState.EmptyView
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
        if ((latestSearchText == searchText && !isRefreshButtonPressed) or (searchText == "")) {
            if (!latestSearchText.isNullOrEmpty()) {
                showSearchEditClearButton.postValue(searchText.isNotEmpty())
            }
            isSearchButtonPressed = false
            isRefreshButtonPressed = false
            return
        }

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTrack(searchText) }

        val postTime : Long?

        if (isSearchButtonPressed || isRefreshButtonPressed) {
            postTime = SystemClock.uptimeMillis()
            isSearchButtonPressed = false
            isRefreshButtonPressed = false
        } else {
            postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        }

        handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )

        showSearchEditClearButton.postValue(searchText.isNotEmpty())
    }

    private fun searchTrack(searchText: String) {
        renderState(
            SearchActivityState.Loading
        )
        this.latestSearchText = searchText

        viewModelScope.launch {
            tracksInteractor
                .searchTracks(searchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
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
                        renderState(
                            SearchActivityState.EmptySearchResult(getApplication<Application>().getString(R.string.nothing_found))
                        )
                    }
                    else -> {
                        renderState(
                            SearchActivityState.Error(getApplication<Application>().getString(R.string.something_went_wrong))
                        )
                    }
                }
            }
            tracks.isEmpty() -> {
                renderState(
                    SearchActivityState.EmptySearchResult(getApplication<Application>().getString(R.string.nothing_found))
                )
            }
            else -> {
                renderState(
                    SearchActivityState.SearchResult(tracks)
                )
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val HISTORY_MIN_SIZE = 0
        private const val EMPTY_TEXT = ""
        private var HAS_FOCUS = false
        private var HAS_SEARCH_TEXT_IS_EMPTY = true
    }
}