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
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchActivityPresenter
import com.example.playlistmaker.search.presentation.SearchView
import com.example.playlistmaker.search.ui.model.SearchActivityState

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

    private var searchActivityPresenter : SearchActivityPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchActivityPresenter = (this.applicationContext as? App)?.searchActivityPresenter
        if (searchActivityPresenter == null) {
            searchActivityPresenter = Creator.provideSearchActivityPresenter(
                this.applicationContext
            )
            (this.applicationContext as? App)?.searchActivityPresenter = searchActivityPresenter
        }

        backButton = findViewById(R.id.ib_back)
        searchClearButton = findViewById(R.id.ivClearButton)
        searchEdit = findViewById(R.id.etSearch)
        searchEdit.imeOptions = EditorInfo.IME_ACTION_SEARCH
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
                searchActivityPresenter?.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }
        searchEditWatcher?.let { searchEdit.addTextChangedListener(it) }

// Проверка находится ли поле поиска в фокусе
        searchEdit.setOnFocusChangeListener { _, hasFocus ->
            searchActivityPresenter?.onFocusChange(hasFocus, searchText.isEmpty())
        }

// Нажатие на клавиатуре кнопки Search
        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchActivityPresenter?.onSearchOrRefreshButtonPress()
                searchActivityPresenter?.searchDebounce(searchText)
            }
            false
        }

// Кнопка очистки поля поиска
        searchClearButton.setOnClickListener {
            searchActivityPresenter?.onClearSearchButtonPress()
        }

// Кнопка "Очистить историю поиска"
        historyClearButton.setOnClickListener {
            searchActivityPresenter?.onClearHistoryButtonPress()
        }

// Кнопка "Обновить"
        placeholderButton.setOnClickListener {
            searchActivityPresenter?.onSearchOrRefreshButtonPress()
            searchActivityPresenter?.searchDebounce(searchText)
        }

// Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        searchActivityPresenter?.attachView(this)
        searchActivityPresenter?.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()

        searchEditWatcher?.let { searchEdit.removeTextChangedListener(it) }
        searchActivityPresenter?.detachView()
        searchActivityPresenter?.onDestroy()

        if (isFinishing) {
            (this.application as? App)?.searchActivityPresenter = null
        }
    }

    override fun onResume() {
        super.onResume()

        searchActivityPresenter?.attachView(this)
        searchActivityPresenter?.onFocusChange(searchEdit.hasFocus(), searchEdit.text.isEmpty())
    }

    override fun onPause() {
        super.onPause()

        searchActivityPresenter?.detachView()
    }

    override fun onStop() {
        super.onStop()

        searchActivityPresenter?.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchActivityPresenter?.detachView()
    }

    override fun onStart() {
        super.onStart()

        searchActivityPresenter?.attachView(this)
    }

    override fun render(state: SearchActivityState) {
        when (state) {
// Состояние показа загрузки
            is SearchActivityState.Loading -> showLoading()
// Состояние показа поиска
            is SearchActivityState.SearchResult -> showContent(state.tracks)
            is SearchActivityState.EmptySearchResult -> showEmptySearchResultPlaceholder(state.message)
// Состояние показа истории
            is SearchActivityState.History -> showHistory(state.tracks)
// Состояние пустого экрана
            is SearchActivityState.EmptyView -> showEmpty()
// Состояние ошибки
            is SearchActivityState.Error -> showErrorPlaceholder(state.message)
        }
    }

    private fun updateTrackListView(tracks: List<Track>?) {
        trackListAdapter.trackList.clear()
        if (tracks != null) trackListAdapter.trackList.addAll(tracks)
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

// Состояние «загрузки»
    private fun showLoading() {
        trackListView.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }
// Состояние «ошибки»
    private fun showErrorPlaceholder(errorMessage: String) {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.GONE
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.ic_something_went_wrong_placeholder)
        placeholderImage.visibility = View.VISIBLE
        placeholderMessage.text = errorMessage
        placeholderMessage.visibility = View.VISIBLE
        placeholderButton.visibility = View.VISIBLE
    }
// Состояние «пустого экрана»
    private fun showEmpty() {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
    }
// Состояние «пустого списка»
    private fun showEmptySearchResultPlaceholder(emptyMessage: String) {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.GONE
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.ic_nothing_found_placeholder)
        placeholderImage.visibility = View.VISIBLE
        placeholderMessage.text = emptyMessage
        placeholderMessage.visibility = View.VISIBLE
    }
// Состояние «контента»
    private fun showContent(tracks: List<Track>?) {
        progressBar.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        historyHint.visibility = View.GONE
        historyClearButton.visibility = View.GONE
        updateTrackListView(tracks)
        trackListView.visibility = View.VISIBLE
    }
// Состояние «истории»
    private fun showHistory(tracks: List<Track>?) {
        progressBar.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        historyHint.visibility = View.VISIBLE
        historyClearButton.visibility = View.VISIBLE
        updateTrackListView(tracks)
        trackListView.visibility = View.VISIBLE
    }

    override fun clearSearchEdit() {
        searchEdit.setText("")
    }

    override fun hideKeyboard() {
        val view = this.currentFocus ?: return
        val method = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun showSearchEditClearButton(isVisible: Boolean) {
        searchClearButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK = "TRACK"
    }
}