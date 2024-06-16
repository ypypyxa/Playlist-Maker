package com.example.playlistmaker.search.ui

import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchView

class SearchActivity : AppCompatActivity(), SearchView {

    private lateinit var backButton: ImageButton
    private lateinit var searchEdit: EditText
    private lateinit var searchClearButton: ImageView
    private lateinit var historyHint: TextView
    private lateinit var historyClearButton: Button
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var trackListView: RecyclerView

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var searchEditWatcher: TextWatcher? = null
    private var searchText = ""

// Инициализируем адаптер
    private val trackListAdapter = TrackListAdapter { item ->
// Нажатие на итем
        if (clickDebounce()) {
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, item)
            startActivity(playerIntent)
        }
    }

    private val searchActivityPresenter = Creator.provideSearchActivityPresenter(
        this,
        this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.ib_back)
        searchClearButton = findViewById(R.id.ivClearButton)
        searchEdit = findViewById(R.id.etSearch)
        historyHint = findViewById(R.id.tvHistoryHint)
        historyClearButton = findViewById(R.id.btnHistoryClear)
        placeholderImage = findViewById(R.id.ivPlaceholderImage)
        placeholderMessage = findViewById(R.id.tvPlaceholderMessage)
        placeholderButton = findViewById(R.id.btnPlaceholderButton)
        progressBar = findViewById(R.id.progressBar)
        trackListView = findViewById(R.id.rvSearchTrackList)

        trackListView.adapter = trackListAdapter

// Наблюдатель событий набора текста
        searchEditWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

// Изменение текста
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                searchActivityPresenter.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }
        searchEditWatcher?.let { searchEdit.addTextChangedListener(it) }

// Проверка находится ли поле поиска в фокусе
        searchEdit.setOnFocusChangeListener { _, hasFocus ->
            searchActivityPresenter.onFocusChange(hasFocus, searchText.isEmpty())
        }

// Нажатие на клавиатуре кнопки Done
        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (clickDebounce()) searchActivityPresenter.searchTrack(searchText)
            }
            false
        }

// Кнопка очистки поля поиска
        searchClearButton.setOnClickListener {
            searchActivityPresenter.onClearSearchButtonPress()
        }

// Кнопка "Очистить историю поиска"
        historyClearButton.setOnClickListener {
            searchActivityPresenter.onClearHistoryButtonPress()
        }

// Кнопка "Обновить"
        placeholderButton.setOnClickListener {
            if (clickDebounce()) searchActivityPresenter.searchTrack(searchText)
        }

// Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        searchActivityPresenter.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()

        searchEditWatcher?.let { searchEdit.removeTextChangedListener(it) }
        searchActivityPresenter.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        searchActivityPresenter.onFocusChange(searchEdit.hasFocus(), searchEdit.text.isEmpty())
    }

    override fun updateTrackListView(tracks: List<Track>) {
        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

// Задержка между кликами
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun clearSearchEdit() {
        searchEdit.setText("")
    }

    override fun hideKeayboard() {
        val view = this.currentFocus ?: return
        val method = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
        searchActivityPresenter.searchTrack(searchText)
    }

    override fun showSearchClearButton(isVisible: Boolean) {
        searchClearButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showHistoryHint(isVisible: Boolean) {
        historyHint.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showHistoryClearButton(isVisible: Boolean) {
        historyClearButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showPlaceholderImage(isVisible: Boolean) {
        placeholderImage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun setPlaceholderImage(drawableID: Int) {
        placeholderImage.setImageResource(drawableID)
    }
    override fun showPlaceholderMessage(isVisible: Boolean) {
        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun setPlaceholderMessage (message: String){
        placeholderMessage.text = message
    }
    override fun showPlaceholderButton(isVisible: Boolean) {
        placeholderButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showTrackListView(isVisible: Boolean) {
        trackListView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK = "TRACK"
    }
}