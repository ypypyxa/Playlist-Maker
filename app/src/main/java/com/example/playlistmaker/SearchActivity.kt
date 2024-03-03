package com.example.playlistmaker

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val HISTORY = "history"
const val HISTORY_TRACKS = "history_tracks"

class SearchActivity : AppCompatActivity() {

//    private lateinit var activityState: ActivityState
    private lateinit var backButton: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var etSearchClearButton: ImageView
    private lateinit var history: SharedPreferences
    private lateinit var historyHint: TextView
    private lateinit var historyClearButton: Button
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var trackListView: RecyclerView

    private var searchTrackList = ArrayList<Track>()
    private var historyTrackList = ArrayList<Track>()
    private val historyGson = Gson()
    private var textValue: String = TEXT
    private val itunesBaseUrl = "https://itunes.apple.com"
    private var activityState: ActivityState? = null

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
    }

//Восстанавливаем предыдущее состояние бандла
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Восстанавливаем текст в поле поиска
        textValue = savedInstanceState.getString(EDITED_TEXT, TEXT)
        etSearch.setText(textValue)

        //Восстановление состояния экрана
        activityState = savedInstanceState.getSerializable(ACTIVITY_STATE) as ActivityState
        when (activityState) {
            ActivityState.SHOW_SEARCH_LIST -> {
                searchTrackList = savedInstanceState.getSerializable(TRACK_LIST) as ArrayList<Track>
                trackListAdapter.trackList = searchTrackList
                trackListView.visibility = View.VISIBLE
            }
            ActivityState.SHOW_HISTORY_LIST -> {
                historyTrackList = loadTrackList()
                trackListAdapter.trackList = historyTrackList
            }
            ActivityState.FAILURE -> showFailureMessage()
            ActivityState.NOTHING_FOUND -> showNothingFoundMessage()
            null -> TODO()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.ib_back)
        etSearchClearButton = findViewById(R.id.iv_clear_button)
        etSearch = findViewById(R.id.et_search)
        historyHint = findViewById(R.id.tv_history_hint)
        historyClearButton = findViewById(R.id.btn_history_clear)
        placeholderImage = findViewById(R.id.iv_placeholder_image)
        placeholderMessage = findViewById(R.id.tv_placeholder_message)
        placeholderButton = findViewById(R.id.btn_placeholder_button)
        trackListView = findViewById(R.id.rv_search_track_list)

        history = getSharedPreferences(HISTORY, MODE_PRIVATE)

//Слушатель нажатия на трек в листе поиска
        val onTrackItemClickListener = object : OnItemClickListener {
//Нажатие на трэк
            override fun onItemClick(item: Track) {
                if (historyTrackList.none { it.trackId == item.trackId }) {
                    historyTrackList.add(0, item)
                } else {
                    historyTrackList.remove(item)
                    historyTrackList.add(0, item)
                }
//                historyTrackList.add(0, item)
                if (historyTrackList.size > 10) {
                    historyTrackList.removeAt(10)
                }
                saveTrackList(historyTrackList)
            }
        }
/*
//Слушатель нажатия на трек в листе истории
        val onHistoryItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                val position = trackList.indexOf(item)
                trackList.remove(item)
                searchListAdapter.notifyItemRemoved(position)
                searchListAdapter.notifyItemRangeChanged(position, trackList.size)
            }
        }
*/

        trackListAdapter = TrackListAdapter(onTrackItemClickListener)

//Проверяем был ли это первый запуск или поворот экрана
        if (savedInstanceState == null) {
            if (history != null) {
                historyTrackList = loadTrackList()
                activityState = ActivityState.SHOW_HISTORY_LIST
            } else {
                trackListAdapter.trackList = searchTrackList
                activityState = ActivityState.SHOW_SEARCH_LIST
            }
        } else {
            if (activityState == ActivityState.SHOW_HISTORY_LIST) {
                trackListAdapter.trackList = historyTrackList
            } else {
                trackListAdapter.trackList = searchTrackList
                activityState = ActivityState.SHOW_SEARCH_LIST
            }
        }

        trackListView.adapter = trackListAdapter

//Наблюдатель событий набора текста
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

//Изменение текста
//Если текстовое поле пустое и
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etSearch.text.isEmpty() && historyTrackList.size != 0) {
                    showHistoryView()
                    trackListAdapter.trackList = historyTrackList
                    trackListAdapter.notifyDataSetChanged()
                    searchTrackList.clear()
                } else if (etSearch.text.isEmpty() && historyTrackList.size == 0) { //если поле и история пустые - скрываются подсказки и список песен
                    hideHistoryView()
                    searchTrackList.clear()
                    trackListAdapter.trackList = searchTrackList
                } else { //пока поле не пустое показывается результат предыдущего поиска
                    hideHistoryView()
                    trackListAdapter.trackList = searchTrackList
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
        etSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && etSearch.text.isEmpty() && historyTrackList.size > 0) showHistoryView() else hideHistoryView()
            trackListAdapter.trackList = historyTrackList
        }

//Нажатие на клавиатуре кнопки Done
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

//Кнопка очистки поля поиска
        etSearchClearButton.setOnClickListener {
            etSearch.setText("")
            hideKeyboard()
            searchTrackList.clear()
            hidePlaceholderView()
            historyTrackList = loadTrackList()
            trackListAdapter.trackList = historyTrackList
            trackListAdapter.notifyDataSetChanged()
            activityState = ActivityState.SHOW_HISTORY_LIST
        }

//Кнопка "Очистить историю поиска"
        historyClearButton.setOnClickListener {
            historyTrackList.clear()
            trackListAdapter.trackList = historyTrackList
            trackListAdapter.notifyDataSetChanged()
            saveTrackList(historyTrackList)
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

// Функция для сохранения trackList в SharedPreferences/history
    private fun saveTrackList(trackList: ArrayList<Track>) {
        history.edit()
            .putString(HISTORY_TRACKS, historyGson.toJson(trackList))
            .apply()
    }

// Функция для загрузки trackList из SharedPreferences/history
    private fun loadTrackList(): ArrayList<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return historyGson.fromJson(history.getString(HISTORY_TRACKS, ""), type) ?: ArrayList()
    }

    private fun searchTrack() {
        trackListAdapter.trackList = searchTrackList
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
                }

                override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
                    searchTrackList.clear()
                    activityState = ActivityState.FAILURE
                    showFailureMessage()
                }
            })
        }
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
        activityState = ActivityState.SHOW_SEARCH_LIST
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
        private const val EDITED_TEXT = "EDITED_TEXT"
        private const val TEXT = ""
        private const val TRACK_LIST = "TRACK_LIST"
        private const val ACTIVITY_STATE = "ACTIVITY_STATE"
    }
}