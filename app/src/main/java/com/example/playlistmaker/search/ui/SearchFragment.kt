package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var searchEditWatcher: TextWatcher? = null
    private var searchText = ""

    private val trackListAdapter = TrackListAdapter { item ->
// Нажатие на итем
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, item)
            startActivity(playerIntent)
        }
    }

    private val searchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEdit.imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
        binding.trackListView.adapter = trackListAdapter

// Наблюдатель событий набора текста
        searchEditWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            // Изменение текста
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                searchViewModel.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: android.text.Editable?) {
                //empty
            }
        }
        searchEditWatcher?.let { binding.searchEdit.addTextChangedListener(it) }

// Проверка находится ли поле поиска в фокусе
        binding.searchEdit.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.onFocusChange(hasFocus, searchText.isEmpty())
        }

// Нажатие на клавиатуре кнопки Search
        binding.searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                searchViewModel.onSearchButtonPress()
                searchViewModel.searchDebounce(searchText)
            }
            false
        }

// Кнопка очистки поля поиска
        binding.searchClearButton.setOnClickListener {
            searchViewModel.onClearSearchButtonPress()
            binding.searchEdit.requestFocus()
        }

// Кнопка "Очистить историю поиска"
        binding.historyClearButton.setOnClickListener {
            searchViewModel.onClearHistoryButtonPress()
        }

// Кнопка "Обновить"
        binding.placeholderButton.setOnClickListener {
            searchViewModel.onRefreshButtonPress()
            searchViewModel.searchDebounce(searchText)
        }

// Кнопка назад
        binding.backButton.setOnClickListener {
            TODO("Сделать переход назад")
        }

        searchViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        searchViewModel.observeToastState().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }
        searchViewModel.observeSearchEditClearButtonState().observe(viewLifecycleOwner) { isVisible ->
            showSearchEditClearButton(isVisible)
        }
        searchViewModel.observeClearSearchEditCommand().observe(viewLifecycleOwner) { emptyText ->
            clearSearchEdit(emptyText)
        }
        searchViewModel.observeHideKeyboardCommand().observe(viewLifecycleOwner) { isVisible ->
            hideKeyboard(isVisible)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        searchEditWatcher?.let { binding.searchEdit.removeTextChangedListener(it) }
        searchViewModel.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        searchViewModel.onFocusChange(binding.searchEdit.hasFocus(), binding.searchEdit.text.isEmpty())
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

    private fun render(state: com.example.playlistmaker.search.ui.model.SearchActivityState) {
        when (state) {
// Состояние показа загрузки
            is com.example.playlistmaker.search.ui.model.SearchActivityState.Loading -> showLoading()
// Состояние показа поиска
            is com.example.playlistmaker.search.ui.model.SearchActivityState.SearchResult -> showContent(state.tracks)
            is com.example.playlistmaker.search.ui.model.SearchActivityState.EmptySearchResult -> showEmptySearchResultPlaceholder(state.message)
// Состояние показа истории
            is com.example.playlistmaker.search.ui.model.SearchActivityState.History -> showHistory(state.tracks)
// Состояние пустого экрана
            is com.example.playlistmaker.search.ui.model.SearchActivityState.EmptyView -> showEmpty()
// Состояние ошибки
            is com.example.playlistmaker.search.ui.model.SearchActivityState.Error -> showErrorPlaceholder(state.message)
        }
    }

    private fun updateTrackListView(tracks: List<com.example.playlistmaker.search.domain.model.Track>?) {
        trackListAdapter.trackList.clear()
        if (tracks != null) trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    // Состояние «загрузки»
    private fun showLoading() {
        binding.trackListView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
        binding.historyHint.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    // Состояние «ошибки»
    private fun showErrorPlaceholder(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.trackListView.visibility = View.GONE
        binding.historyHint.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        binding.placeholderImage.setImageResource(com.example.playlistmaker.R.drawable.ic_something_went_wrong_placeholder)
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderMessage.text = errorMessage
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderButton.visibility = View.VISIBLE
    }
    // Состояние «пустого экрана»
    private fun showEmpty() {
        binding.searchClearButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.trackListView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.historyHint.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
    }
    // Состояние «пустого списка»
    private fun showEmptySearchResultPlaceholder(emptyMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.trackListView.visibility = View.GONE
        binding.historyHint.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        binding.placeholderImage.setImageResource(com.example.playlistmaker.R.drawable.ic_nothing_found_placeholder)
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderMessage.text = emptyMessage
        binding.placeholderMessage.visibility = View.VISIBLE
    }
    // Состояние «контента»
    private fun showContent(tracks: List<com.example.playlistmaker.search.domain.model.Track>?) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.historyHint.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        updateTrackListView(tracks)
        binding.trackListView.visibility = View.VISIBLE
    }
    // Состояние «истории»
    private fun showHistory(tracks: List<com.example.playlistmaker.search.domain.model.Track>?) {
        binding.searchClearButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.historyHint.visibility = View.VISIBLE
        binding.historyClearButton.visibility = View.VISIBLE
        updateTrackListView(tracks)
        binding.trackListView.visibility = View.VISIBLE
    }

    private fun hideKeyboard(isVisible: Boolean) {
        if (isVisible) {
            val view = requireActivity().currentFocus ?: return
            val method = requireContext().getSystemService(androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            method.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showSearchEditClearButton(isVisible: Boolean) {
        binding.searchClearButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    private fun clearSearchEdit(emptyText: String) {
        binding.searchEdit.setText(emptyText)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK = "TRACK"
    }
}