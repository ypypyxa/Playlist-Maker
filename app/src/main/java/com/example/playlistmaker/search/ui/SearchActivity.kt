package com.example.playlistmaker.search.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    //Инициализируем адаптер
    private val trackListAdapter = TrackListAdapter { item ->
//Нажатие на итем
        if (clickDebounce()) {
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, item)
            startActivity(playerIntent)
        }
    }

    private val searchActivityController = Creator.provideSearchActivityController(this, trackListAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchActivityController.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()

        searchActivityController.onDestroy()
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK = "TRACK"
    }
}