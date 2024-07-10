package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchActivityState
import com.example.playlistmaker.utils.SingleLiveEvent

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())

    private lateinit var historyInteractor: HistoryInteractor
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

    fun onCreate() {
        historyInteractor = Creator.provideHistoryInteractor(getApplication())
    }

    fun onDestroy() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun renderState(state: SearchActivityState) {
        searchActivityLiveData.postValue(state)
    }

    fun onFocusChange(hasFocus: Boolean, searchTextIsEmpty: Boolean) {
        historyTracks = historyInteractor.loadTracks()
        if ( hasFocus && searchTextIsEmpty && historyTracks.size != HISTORY_MIN_SIZE) {
            renderState(
               SearchActivityState.History(historyTracks)
            )
        }
    }

    fun onClearSearchButtonPress() {
        clearSearchEdit.postValue(EMPTY_TEXT)
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
        if (latestSearchText == searchText && !isRefreshButtonPressed) {
            if (!latestSearchText.isNullOrEmpty()) {
                showSearchEditClearButton.postValue(searchText.isNotEmpty())
            }
            isSearchButtonPressed = false
            isRefreshButtonPressed = false
            return
        }

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        if (searchText.isEmpty()) {
            if (historyTracks.size != HISTORY_MIN_SIZE) {
// если поле пустое, а история поиска не пустая - скрывается заглушка
// и показывается история поиска
                renderState(
                    SearchActivityState.History(historyTracks)
                )
            } else {
// если поле и история пустые - скрываются подсказки и список песен
                renderState(
                    SearchActivityState.EmptyView
                )
            }
            isSearchButtonPressed = false
        } else {
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
        }

        showSearchEditClearButton.postValue(searchText.isNotEmpty())
    }

    private fun searchTrack(searchText: String) {
        renderState(
            SearchActivityState.Loading
        )
        this.latestSearchText = searchText

        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                val tracks = ArrayList<Track>()

                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }
                when {
                    errorMessage != null -> {
                        showToast.postValue(errorMessage)
                        renderState(
                            SearchActivityState.Error(getApplication<Application>().getString(R.string.something_went_wrong))
                        )
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
        })
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val HISTORY_MIN_SIZE = 0
        private const val EMPTY_TEXT = ""

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchActivityViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}