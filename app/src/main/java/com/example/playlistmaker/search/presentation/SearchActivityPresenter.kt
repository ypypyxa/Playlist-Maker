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

class SearchActivityPresenter(
    private val searchView: SearchView,
    private val context: Context,
) {

    private val handler = Handler(Looper.getMainLooper())
    private var isSearchButtonPressed = false

    private lateinit var history: SharedPreferences
    private lateinit var historyInteractor: HistoryInteractor

    private var searchTracks = ArrayList<Track>()
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
            showHistoryView()
            searchView.updateTrackListView(historyTracks)
        } else {
            if (!searchTextIsEmpty) {
                searchView.showTrackListView(true)
            }
        }
    }

    fun onClearSearchButtonPress() {
        searchView.clearSearchEdit()
        searchTracks.clear()
        hidePlaceholderView()
        historyTracks = historyInteractor.loadTracks()
        if (historyTracks.size > HISTORY_MIN_SIZE) {
            searchView.updateTrackListView(historyTracks)
            showHistoryView()
        } else {
            hideHistoryView()
        }
    }

    fun onClearHistoryButtonPress() {
        historyTracks.clear()
        searchView.updateTrackListView(historyTracks)
        historyInteractor.clearTracks()
        hideHistoryView()
    }

    fun onSearchButtonPress() {
        isSearchButtonPressed = true
    }

    private fun searchTrack(searchText: String) {
        hidePlaceholderView()
        hideHistoryView()
        searchView.showProgressBar(true)
        trackInteractor.searchTracks(searchText, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                handler.post {
                    searchView.showProgressBar(false)
                    if (foundTracks != null) {
                        searchView.updateTrackListView(foundTracks)
                        searchView.showTrackListView(true)
                    }
                    if (errorMessage != null) {
                        showFailureMessage()
                    } else if (foundTracks.isNullOrEmpty()) {
                        showNothingFoundMessage()
                    } else {
                        hidePlaceholderView()
                    }
                }
            }
        })
    }

    fun searchDebounce(searchText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        if (searchText.isEmpty()) {
            if (historyTracks.size != HISTORY_MIN_SIZE) {
// если поле пустое, а история поиска не пустая - скрывается заглушка
// и показывается история поиска
                searchTracks.clear()
                searchView.updateTrackListView(historyTracks)

                hidePlaceholderView()
                showHistoryView()
            } else {
// если поле и история пустые - скрываются подсказки и список песен
                hideHistoryView()

                searchTracks.clear()
                searchView.updateTrackListView(searchTracks)
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
        searchView.showSearchClearButton(searchText.isNotEmpty())
    }

    private fun showHistoryView() {
        searchView.showHistoryHint(true)
        searchView.showHistoryClearButton(true)
        searchView.showTrackListView(true)
    }

    private fun hideHistoryView() {
        searchView.showHistoryHint(false)
        searchView.showHistoryClearButton(false)
        searchView.showTrackListView(false)
    }

    fun hidePlaceholderView() {
        searchView.showPlaceholderImage(false)
        searchView.showPlaceholderMessage(false)
        searchView.showPlaceholderButton(false)
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            searchTracks.clear()
            searchView.updateTrackListView(searchTracks)

            if (additionalMessage.isNotEmpty()) {
                searchView.showMessage(additionalMessage)
            }
        } else {
            searchView.showPlaceholderMessage(false)
        }
    }

    fun showFailureMessage() {
        searchView.setPlaceholderImage(R.drawable.ic_something_went_wrong_placeholder)
        searchView.setPlaceholderMessage(context.getString(R.string.something_went_wrong))
        searchView.showTrackListView(false)
        searchView.showPlaceholderImage(true)
        searchView.showPlaceholderMessage(true)
        searchView.showPlaceholderButton(true)
    }

    fun showNothingFoundMessage() {
        searchView.setPlaceholderImage(R.drawable.ic_failure_placeholder)
        searchView.setPlaceholderMessage(context.getString(R.string.nothing_found))
        searchView.showTrackListView(false)
        searchView.showPlaceholderImage(true)
        searchView.showPlaceholderMessage(true)
        searchView.showPlaceholderButton(false)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val HISTORY = "history"
        private const val HISTORY_MIN_SIZE = 0
        private const val HISTORY_MAX_SIZE = 10
    }
}