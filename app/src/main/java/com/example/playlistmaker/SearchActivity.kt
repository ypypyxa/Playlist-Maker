package com.example.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var clearButton: ImageView
    private lateinit var trackListView: RecyclerView
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var etSearch: EditText
    private lateinit var activityState: ActivityState

    private var trackList = ArrayList<Track>()
    private var textValue: String = TEXT
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val musicService = retrofit.create(MusicApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderImage = findViewById(R.id.iv_placeholder_image)
        placeholderMessage = findViewById(R.id.tv_placeholder_message)
        placeholderButton = findViewById(R.id.btn_placeholder_button)
        backButton = findViewById(R.id.ib_back)
        clearButton = findViewById(R.id.iv_clear_button)
        etSearch = findViewById(R.id.et_search)
        trackListAdapter = TrackListAdapter()
        trackListView = findViewById(R.id.rv_search_track_list)

        trackListAdapter.trackList = trackList
        trackListView.adapter = trackListAdapter

        activityState = ActivityState.SHOW_LIST

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            etSearch.setText("")
            hideKeyboard()
            trackList.clear()
            trackListView.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
        }

        placeholderButton.setOnClickListener {
            searchTrack()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }
        etSearch.addTextChangedListener(simpleTextWatcher)

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }
    }

    private fun searchTrack() {
        if (etSearch.text.isNotEmpty()) {
            musicService.findTrack(etSearch.text.toString()).enqueue(object :
                Callback<SongsResponse> {
                override fun onResponse(
                    call: Call<SongsResponse>,
                    response: Response<SongsResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackListAdapter.notifyDataSetChanged()
                            activityState = ActivityState.SHOW_LIST
                        }
                        if (trackList.isEmpty()) {
//                            showMessage(getString(R.string.nothing_found), "")
                            trackList.clear()
                            activityState = ActivityState.NOTHING_FOUND
                            showNothingFoundMessage()
                        } else {
//                            showMessage("", "")
                            activityState = ActivityState.SHOW_LIST
                            trackListView.visibility = View.VISIBLE
                            placeholderImage.visibility = View.GONE
                            placeholderMessage.visibility = View.GONE
                            placeholderButton.visibility = View.GONE
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
//                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    trackList.clear()
                    activityState = ActivityState.FAILURE
                    showFailureMessage()
                }
            })
        }
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
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: return
        val method = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDITED_TEXT, textValue)

        // Сохранение списка песен
        outState.putSerializable(TRACK_LIST, trackList)

        // Сохраняем состояние экрана
        outState.putSerializable(ACTIVITY_STATE, activityState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Вторым параметром мы передаём значение по умолчанию
        textValue = savedInstanceState.getString(EDITED_TEXT, TEXT)
        etSearch.setText(textValue)

        //Восстановление состояния экрана
        activityState = savedInstanceState.getSerializable(ACTIVITY_STATE) as ActivityState
        when (activityState) {
            ActivityState.SHOW_LIST -> {
                trackList = savedInstanceState.getSerializable(TRACK_LIST) as ArrayList<Track>
                trackListAdapter.trackList = trackList
            }
            ActivityState.FAILURE -> showFailureMessage()
            ActivityState.NOTHING_FOUND -> showNothingFoundMessage()
        }

    }
    companion object {
        private const val EDITED_TEXT = "EDITED_TEXT"
        private const val TEXT = ""
        private const val TRACK_LIST = "TRACK_LIST"
        private const val ACTIVITY_STATE = "ACTIVITY_STATE"
    }
}