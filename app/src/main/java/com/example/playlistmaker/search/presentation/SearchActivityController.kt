package com.example.playlistmaker.search.presentation

import android.content.Context.MODE_PRIVATE
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.search.ui.TrackListAdapter

class SearchActivityController(
    private val searchActivity: SearchActivity,
    private val trackListAdapter: TrackListAdapter
) {
    private lateinit var backButton: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var etSearchClearButton: ImageView
    private lateinit var historyHint: TextView
//    private lateinit var historyManger: HistoryRepositoryImpl
    private lateinit var historyClearButton: Button
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTrackList: RecyclerView

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }

    private var searchTracks: MutableList<Track> = mutableListOf()
    private var historyTracks = ArrayList<Track>()


    private val trackInteractor = Creator.provideTrackInteractor(searchActivity)

    fun onCreate() {

        backButton = searchActivity.findViewById(R.id.ib_back)
        etSearchClearButton = searchActivity.findViewById(R.id.ivClearButton)
        etSearch = searchActivity.findViewById(R.id.etSearch)
        historyHint = searchActivity.findViewById(R.id.tvHistoryHint)
        historyClearButton = searchActivity.findViewById(R.id.btnHistoryClear)
        placeholderImage = searchActivity.findViewById(R.id.ivPlaceholderImage)
        placeholderMessage = searchActivity.findViewById(R.id.tvPlaceholderMessage)
        placeholderButton = searchActivity.findViewById(R.id.btnPlaceholderButton)
        progressBar = searchActivity.findViewById(R.id.progressBar)
        tvTrackList = searchActivity.findViewById(R.id.rvSearchTrackList)

        tvTrackList.adapter = trackListAdapter

        val history = searchActivity.getSharedPreferences(HISTORY, MODE_PRIVATE)
        val historyInteractor = Creator.provideHistoryInteractor(history)

        //Наблюдатель событий набора текста
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            //Изменение текста
//Если текстовое поле пустое, а история нет, то отображается подсказка и история поиска
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etSearch.text.isEmpty() && historyTracks.size != HISTORY_MIN_SIZE) {
                    trackListAdapter.trackList = historyTracks
                    trackListAdapter.notifyDataSetChanged()
                    searchTracks.clear()
                    hidePlaceholderView()
                    showHistoryView()
                } else if (etSearch.text.isEmpty() && historyTracks.size == HISTORY_MIN_SIZE) { //если поле и история пустые - скрываются подсказки и список песен
                    hideHistoryView()
                    searchTracks.clear()
                    trackListAdapter.trackList = searchTracks
                } else { //пока поле не пустое показывается результат предыдущего поиска
                    hideHistoryView()
                    searchDebounce()
                    trackListAdapter.trackList = searchTracks
                    trackListAdapter.notifyDataSetChanged()
                    tvTrackList.visibility = View.VISIBLE
                }
                etSearchClearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }
        etSearch.addTextChangedListener(textWatcher)

//Проверка находится ли поле поиска в фокусе
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            historyTracks = historyInteractor.loadTracks()
            if ( hasFocus && etSearch.text.isEmpty() && historyTracks.size != HISTORY_MIN_SIZE) {
                showHistoryView()
                trackListAdapter.trackList = historyTracks
                trackListAdapter.notifyDataSetChanged()
            } else {
                hideHistoryView()
            }

        }

//Нажатие на клавиатуре кнопки Done
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }

//Кнопка очистки поля поиска
        etSearchClearButton.setOnClickListener {
            etSearch.setText("")
            hideKeyboard()
            searchTracks.clear()
            hidePlaceholderView()
            historyTracks = historyInteractor.loadTracks()
            if (historyTracks.size > HISTORY_MIN_SIZE) {
                trackListAdapter.trackList = historyTracks
                trackListAdapter.notifyDataSetChanged()
                showHistoryView()
            } else {
                hideHistoryView()
            }
        }

//Кнопка "Очистить историю поиска"
        historyClearButton.setOnClickListener {
            historyTracks.clear()
            trackListAdapter.trackList = historyTracks
            trackListAdapter.notifyDataSetChanged()
            historyInteractor.clearTracks()
            hideHistoryView()
        }

//Кнопка "Обновить"
        placeholderButton.setOnClickListener {
            searchTrack()
        }

//Кнопка назад
        backButton.setOnClickListener {
            searchActivity.finish()
        }
    }

    fun onDestroy(){

    }

    private fun searchTrack() {
        if (etSearch.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            trackInteractor.searchTracks(etSearch.text.toString(), object : TrackInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        progressBar.visibility = View.GONE
                        if (foundTracks != null) {
                            searchTracks.clear()
                            searchTracks.addAll(foundTracks)
                            trackListAdapter.notifyDataSetChanged()
                            tvTrackList.visibility = View.VISIBLE
                        }
                        if (errorMessage != null) {
                            showFailureMessage()
                        } else if (searchTracks.isEmpty()) {
                            showNothingFoundMessage()
                        } else {
                            hidePlaceholderView()
                        }
                    }
                }
            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showHistoryView() {
        historyHint.visibility = View.VISIBLE
        historyClearButton.visibility = View.VISIBLE
        tvTrackList.visibility = View.VISIBLE
    }

    private fun hideHistoryView() {
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
        tvTrackList.visibility = View.GONE
    }

    private fun hidePlaceholderView() {
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(searchActivity.applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun showFailureMessage() {
        tvTrackList.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.ic_something_went_wrong_placeholder)
        placeholderMessage.text = searchActivity.getString(R.string.something_went_wrong)
        placeholderImage.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        placeholderButton.visibility = View.VISIBLE
    }

    private fun showNothingFoundMessage() {
        tvTrackList.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.ic_failure_placeholder)
        placeholderMessage.text = searchActivity.getString(R.string.nothing_found)
        placeholderMessage.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        placeholderButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val view = searchActivity.currentFocus ?: return
        val method = searchActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val HISTORY = "history"
        private const val HISTORY_MIN_SIZE = 0
        private const val HISTORY_MAX_SIZE = 10
    }
}