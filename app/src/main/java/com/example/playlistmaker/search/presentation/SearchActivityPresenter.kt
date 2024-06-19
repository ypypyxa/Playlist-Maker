package com.example.playlistmaker.search.presentation

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.history.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchActivityState

class SearchActivityPresenter(
    private val context: Context
) {

    private var searchView: SearchView? = null
    private var searchActivityState: SearchActivityState? = null
    private var latestSearchText: String? = null
    fun attachView(view: SearchView) {
        searchView = view
        searchActivityState?.let { view.render((it)) }
    }
    fun detachView() {
        searchView = null
    }
    fun renderState(state: SearchActivityState) {
        this.searchActivityState = state
        this.searchView?.render(state)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isSearchButtonPressed = false

    private lateinit var history: SharedPreferences
    private lateinit var historyInteractor: HistoryInteractor

    private var historyTracks = ArrayList<Track>()

    private val trackInteractor = Creator.provideTrackInteractor(context)

    fun onCreate() {
        history = context.getSharedPreferences(HISTORY, MODE_PRIVATE)
        historyInteractor = Creator.provideHistoryInteractor(history)
    }

    fun onDestroy() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
        searchView?.clearSearchEdit()
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
    }

    fun onSearchOrRefreshButtonPress() {
        isSearchButtonPressed = true
        searchView?.hideKeyboard()
    }

    private fun searchTrack(searchText: String) {
        renderState(
            SearchActivityState.Loading
        )
        this.latestSearchText = searchText

        trackInteractor.searchTracks(searchText, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                val tracks = ArrayList<Track>()
                handler.post {
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }
                    when {
                        errorMessage != null -> {
                            searchView?.showToast(errorMessage)
                            renderState(
                                SearchActivityState.Error(context.getString(R.string.something_went_wrong))
                            )
                        }
                        tracks.isEmpty() -> {
                            renderState(
                                SearchActivityState.EmptySearchResult(context.getString(R.string.nothing_found))
                            )
                        }
                        else -> {
                            renderState(
                                SearchActivityState.SearchResult(tracks)
                            )
                        }
                    }
                }
            }
        })
    }

    fun searchDebounce(searchText: String) {
        if (latestSearchText == searchText) {
            if (!latestSearchText.isNullOrEmpty()) {
                searchView?.showSearchEditClearButton(searchText.isNotEmpty())
            }
            isSearchButtonPressed = false
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

            var postTime: Long? = null

            if (isSearchButtonPressed) {
                postTime = SystemClock.uptimeMillis()
                isSearchButtonPressed = false
            } else {
                postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            }

            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }

        searchView?.showSearchEditClearButton(searchText.isNotEmpty())
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val HISTORY = "history"
        private const val HISTORY_MIN_SIZE = 0
        private const val HISTORY_MAX_SIZE = 10
    }
}