package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var etSearchClearButton: ImageView
    private lateinit var history: SharedPreferences
    private lateinit var historyHint: TextView
    private lateinit var historyManger: HistoryManager
    private lateinit var historyClearButton: Button
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var playerIntent: Intent
    private lateinit var progressBar: ProgressBar
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var trackListView: RecyclerView

    private var activityState: ActivityState? = null
    private var cursorPosition: Int = 0
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }
    private var searchTrackList = ArrayList<Track>()
    private var historyTrackList = ArrayList<Track>()
    private var textValue: String = TEXT
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val musicService = retrofit.create(MusicApi::class.java)

//Сохраняем состояние бандла
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDITED_TEXT, textValue)

        // Сохранение списка песен
        outState.putSerializable(TRACK_LIST, trackListAdapter.trackList)

        // Сохраняем состояние экрана
        outState.putSerializable(ACTIVITY_STATE, activityState)

        // Сохраняем текущее положение каретки
        outState.putInt(CURSOR_POSITION, etSearch.selectionStart)
    }

//Восстанавливаем предыдущее состояние бандла
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Восстанавливаем текст в поле поиска
        textValue = savedInstanceState.getString(EDITED_TEXT, TEXT)
        etSearch.setText(textValue)

        // Восстанавливаем положение каретки
        cursorPosition = savedInstanceState.getInt(CURSOR_POSITION)
        etSearch.setSelection(cursorPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.ib_back)
        etSearchClearButton = findViewById(R.id.ivClearButton)
        etSearch = findViewById(R.id.etSearch)
        historyHint = findViewById(R.id.tvHistoryHint)
        historyClearButton = findViewById(R.id.btnHistoryClear)
        placeholderImage = findViewById(R.id.ivPlaceholderImage)
        placeholderMessage = findViewById(R.id.tvPlaceholderMessage)
        placeholderButton = findViewById(R.id.btnPlaceholderButton)
        progressBar = findViewById(R.id.progressBar)
        trackListView = findViewById(R.id.rvSearchTrackList)

        playerIntent = Intent(this, PlayerActivity::class.java)

        history = getSharedPreferences(HISTORY, MODE_PRIVATE)
        historyManger = HistoryManager(history)

//Инициализируем адаптер
        trackListAdapter = TrackListAdapter { item ->
//Нажатие на итем
            if (clickDebounce()) {
                if (historyTrackList.none { it.trackId == item.trackId }) {
                    historyTrackList.add(0, item)
                    playerIntent.putExtra(TRACK, item)
                    startActivity(playerIntent)
                } else {
                    historyTrackList.remove(item)
                    historyTrackList.add(0, item)
                    playerIntent.putExtra(TRACK, item)
                    startActivity(playerIntent)
                }
                if (historyTrackList.size > HISTORY_MAX_SIZE) {
                    historyTrackList.removeAt(HISTORY_MAX_SIZE)
                }
                historyManger.saveTrackList(historyTrackList)
                trackListAdapter.notifyDataSetChanged()
            }
        }

//Проверяем был ли это первый запуск или поворот экрана
        if (savedInstanceState == null) {
            historyTrackList = historyManger.loadTrackList()
            activityState = ActivityState.SHOW_NOTHING
        } else {
            activityState = savedInstanceState.getSerializable(ACTIVITY_STATE) as ActivityState
            when (activityState) {
                ActivityState.SHOW_SEARCH_LIST -> {
                    searchTrackList = savedInstanceState.getSerializable(TRACK_LIST) as ArrayList<Track>
                    trackListAdapter.trackList = searchTrackList
                    trackListView.visibility = View.VISIBLE
                }
                ActivityState.SHOW_HISTORY_LIST -> {
                    historyTrackList = historyManger.loadTrackList()
                    trackListAdapter.trackList = historyTrackList
                    showHistoryView()
                }
                ActivityState.FAILURE -> showFailureMessage()
                ActivityState.NOTHING_FOUND -> showNothingFoundMessage()
                ActivityState.SHOW_NOTHING -> hideHistoryView()
                else -> {}
            }
        }

        trackListView.adapter = trackListAdapter

//Наблюдатель событий набора текста
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

//Изменение текста
//Если текстовое поле пустое, а история нет, то отображается подсказка и история поиска
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etSearch.text.isEmpty() && historyTrackList.size != HISTORY_MIN_SIZE) {
                    activityState = ActivityState.SHOW_HISTORY_LIST
                    trackListAdapter.trackList = historyTrackList
                    trackListAdapter.notifyDataSetChanged()
                    searchTrackList.clear()
                    hidePlaceholderView()
                    showHistoryView()
                } else if (etSearch.text.isEmpty() && historyTrackList.size == HISTORY_MIN_SIZE) { //если поле и история пустые - скрываются подсказки и список песен
                    hideHistoryView()
                    activityState = ActivityState.SHOW_NOTHING
                    searchTrackList.clear()
                    trackListAdapter.trackList = searchTrackList
                } else { //пока поле не пустое показывается результат предыдущего поиска
                    hideHistoryView()
                    searchDebounce()
                    trackListAdapter.trackList = searchTrackList
                    trackListAdapter.notifyDataSetChanged()
                    trackListView.visibility = View.VISIBLE
                }
                etSearchClearButton.isVisible = !s.isNullOrEmpty()
                textValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }
        etSearch.addTextChangedListener(textWatcher)

//Проверка находится ли поле поиска в фокусе
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            historyTrackList = historyManger.loadTrackList()
            if ( hasFocus && etSearch.text.isEmpty() && historyTrackList.size != HISTORY_MIN_SIZE) {
                showHistoryView()
                trackListAdapter.trackList = historyTrackList
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
            searchTrackList.clear()
            hidePlaceholderView()
            historyTrackList = historyManger.loadTrackList()
            if (historyTrackList.size > HISTORY_MIN_SIZE) {
                trackListAdapter.trackList = historyTrackList
                trackListAdapter.notifyDataSetChanged()
                activityState = ActivityState.SHOW_HISTORY_LIST
                showHistoryView()
            } else {
                hideHistoryView()
            }
        }

//Кнопка "Очистить историю поиска"
        historyClearButton.setOnClickListener {
            historyTrackList.clear()
            trackListAdapter.trackList = historyTrackList
            trackListAdapter.notifyDataSetChanged()
            historyManger.clearTrackList()
            hideHistoryView()
        }

//Кнопка "Обновить"
        placeholderButton.setOnClickListener {
            searchTrack()
        }

//Кнопка назад
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun searchTrack() {
        progressBar.visibility = View.VISIBLE
        if (etSearch.text.isNotEmpty()) {
            musicService.findTrack(etSearch.text.toString()).enqueue(object :
                Callback<SongsResponse> {
                override fun onResponse(
                    call: Call<SongsResponse>,
                    response: Response<SongsResponse>
                ) {
                    if (response.code() == 200) {
                        searchTrackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            searchTrackList.addAll(response.body()?.results!!)
                            trackListAdapter.notifyDataSetChanged()
                            activityState = ActivityState.SHOW_SEARCH_LIST
                        }
                        if (searchTrackList.isEmpty()) {
                            searchTrackList.clear()
                            activityState = ActivityState.NOTHING_FOUND
                            showNothingFoundMessage()
                        } else {
                            activityState = ActivityState.SHOW_SEARCH_LIST
                            trackListView.visibility = View.VISIBLE
                            hidePlaceholderView()
                        }
                    } else {
                        showFailureMessage()
                        showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                    }
                    progressBar.visibility = View.GONE
                }

                override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
                    searchTrackList.clear()
                    activityState = ActivityState.FAILURE
                    showFailureMessage()
                    progressBar.visibility = View.GONE
                }
            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showHistoryView() {
        historyHint.visibility = View.VISIBLE
        historyClearButton.visibility = View.VISIBLE
        trackListView.visibility = View.VISIBLE
        activityState = ActivityState.SHOW_HISTORY_LIST
    }

    private fun hideHistoryView() {
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
        trackListView.visibility = View.GONE
    }

    private fun hidePlaceholderView() {
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun showFailureMessage() {
        trackListView.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.ic_something_went_wrong_placeholder)
        placeholderMessage.text = getString(R.string.something_went_wrong)
        placeholderImage.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        placeholderButton.visibility = View.VISIBLE
    }

    private fun showNothingFoundMessage() {
        trackListView.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.ic_failure_placeholder)
        placeholderMessage.text = getString(R.string.nothing_found)
        placeholderMessage.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        placeholderButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: return
        val method = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val ACTIVITY_STATE = "ACTIVITY_STATE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CURSOR_POSITION = "cursorPosition"
        private const val HISTORY = "history"
        private const val HISTORY_MIN_SIZE = 0
        private const val HISTORY_MAX_SIZE = 10
        private const val EDITED_TEXT = "EDITED_TEXT"
        private const val TEXT = ""
        private const val TRACK_LIST = "TRACK_LIST"
        private const val TRACK = "TRACK"
    }
}