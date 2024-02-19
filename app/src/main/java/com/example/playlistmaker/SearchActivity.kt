package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

    private lateinit var placeholderMessage: TextView
    private lateinit var backButton: ImageButton
    private lateinit var clearButton: ImageView
    private lateinit var trackListView: RecyclerView
    private lateinit var trackListAdapter: TrackListAdapter
    private var etSearch: EditText? = null

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val musicService = retrofit.create(MusicApi::class.java)

    private val trackList = ArrayList<Track>()

    private var textValue: String = TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        backButton = findViewById(R.id.ib_back)
        clearButton = findViewById(R.id.iv_clear_button)
        etSearch = findViewById(R.id.et_search)
        trackListAdapter = TrackListAdapter()
        trackListView = findViewById(R.id.rv_search_track_list)

        trackListAdapter.trackList = trackList
        trackListView.adapter = trackListAdapter

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            etSearch?.setText("")
            hideKeyboard()
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
        etSearch?.addTextChangedListener(simpleTextWatcher)

        etSearch?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (textValue.isNotEmpty()) {
                    musicService.findTrack(textValue).enqueue(object :
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
                                }
                                if (trackList.isEmpty()) {
                                    showMessage(getString(R.string.nothing_found), "")
                                } else {
                                    showMessage("", "")
                                }
                            } else {
                                showMessage(
                                    getString(R.string.something_went_wrong),
                                    response.code().toString()
                                )
                            }
                        }

                        override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
                            showMessage(getString(R.string.something_went_wrong), t.message.toString())
                        }
                    })
                }
                true
            }
            false
        }
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: return
        val method = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        method.hideSoftInputFromWindow(view.windowToken, 0)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDITED_TEXT, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Вторым параметром мы передаём значение по умолчанию
        textValue = savedInstanceState.getString(EDITED_TEXT, TEXT)
        etSearch?.setText(textValue)
    }
    companion object {
        private const val EDITED_TEXT = "EDITED_TEXT"
        private const val TEXT = ""
    }
}