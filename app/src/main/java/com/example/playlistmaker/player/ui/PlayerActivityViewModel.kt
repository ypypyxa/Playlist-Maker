package com.example.playlistmaker.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.model.PlayerActivityState
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.SingleLiveEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerActivityViewModel(
    private val historyInteractor: HistoryInteractor,
    private val tracksInteractor: TracksInteractor,
    private val mediaPlayer: MediaPlayerInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private lateinit var track: Track

    private val playerActivityLiveData = MutableLiveData<PlayerActivityState>()
    fun observeState(): LiveData<PlayerActivityState> = playerActivityLiveData

    private val showError = SingleLiveEvent<Pair<String, String>>()
    fun observeToastState(): LiveData<Pair<String, String>> = showError

    private val addInFavorite = SingleLiveEvent<Boolean>()
    fun observeAddInFavorite(): LiveData<Boolean> = addInFavorite

// Объявление перменных для таймера воспроизведения
    private lateinit var handler: Handler
    private val updateTime = object : Runnable {
        override fun run() {
            renderState(
                PlayerActivityState.UpdateTimer(
                    mediaPlayer.getCurrentPosition()
                )
            )
            playerState = mediaPlayer.getPlayerState()
            handler.postDelayed(this, DELAY) // Обновляем каждую секунду
            if (playerState == PlayerState.STATE_COMPLETE) {
                stopPlayer()
            }
        }
    }

    private var playerState = PlayerState.STATE_DEFAULT

    fun onCreate(receivedTrack: Track) {
        this.track = receivedTrack

// Лучшего места что бы сохранить трек в историю поиска я пока не нашел...
        historyListUpdate()

        handler = Handler(Looper.getMainLooper())

        val artworkUrl512 = track.artworkUrl100.replaceAfterLast(DELIMITER, "$BIG_SIZE.jpg")

        val albumGroupIsVisible: Boolean = track.collectionName.isNotEmpty()
        val countryGroupIsVisible: Boolean = track.releaseDate.isNotEmpty()
        val genreGroupIsVisible: Boolean = track.primaryGenreName.isNotEmpty()
        val releaseGroupIsVisible: Boolean = track.country.isNotEmpty()
        val trackTimeGroupIsVisible: Boolean = track.previewUrl.isNotEmpty()

        renderState(
            PlayerActivityState.Prepare(
                track,
                artworkUrl512,
                albumGroupIsVisible,
                countryGroupIsVisible,
                genreGroupIsVisible,
                releaseGroupIsVisible,
                trackTimeGroupIsVisible
            )
        )

// Подготавливаем плеер
        preparePlayer()
    }

    fun onPause() {
        pausePlayer()
        handler.removeCallbacks(updateTime)
    }

    fun onDestroy() {
        handler.removeCallbacks(updateTime)
        mediaPlayer.release()
    }

    private fun historyListUpdate() {
        val historyTracks = historyInteractor.loadTracks()
        var index: Int? = null

        if (historyTracks.isEmpty()) {
            historyTracks.add(0, track)
        } else {
            for (i in 0 .. historyTracks.size-1) {
                if (historyTracks[i].trackId == track.trackId) {
                    index = i
                }
            }
            if (index != null) { historyTracks.remove(historyTracks[index]) }
            historyTracks.add(0, track)
        }
        if (historyTracks.size > HISTORY_MAX_SIZE) {
            historyTracks.removeAt(HISTORY_MAX_SIZE)
        }
        historyInteractor.saveTracks(historyTracks)
    }

    private fun renderState(state: PlayerActivityState) {
        playerActivityLiveData.postValue(state)
    }

    fun toggleFavorite() {
        if (track.inFavorite) {
            track.inFavorite = false
            tracksInteractor.removeFromFavorites(track)
            addInFavorite.postValue(false)
        } else {
            track.inFavorite = true
            tracksInteractor.addToFavorites(track)
            addInFavorite.postValue(true)
        }
        historyListUpdate()
    }

    fun playbackControl() {
        when(playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }

    private fun preparePlayer() {
        if (track.previewUrl.isNotEmpty()) {
            mediaPlayer.prepare(track.previewUrl)
            playerState = PlayerState.STATE_PREPARED
        } else {
            renderState(
                PlayerActivityState.FileNotFound
            )
            val error = getApplication<Application>().getString(R.string.player_error)
            val errorMessage = getApplication<Application>().getString(R.string.empty_track_url)
            showError.postValue(Pair(error, errorMessage))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        renderState(
            PlayerActivityState.Play(true)
        )
        playerState = PlayerState.STATE_PLAYING
        handler.post(updateTime)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        renderState(
            PlayerActivityState.Pause(true)
        )
        playerState = PlayerState.STATE_PAUSED
        handler.removeCallbacks(updateTime)
    }

    private fun stopPlayer() {
        renderState(
            PlayerActivityState.Stop(true)
        )
        playerState = PlayerState.STATE_PREPARED
        handler.removeCallbacks(updateTime)
    }

    fun getYear(date: String) : String {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).parse(date)!!
        return calendar.get(Calendar.YEAR).toString()
    }

    companion object {
        private const val BIG_SIZE = "512x512"
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private const val DELIMITER = '/'
        private const val DELAY = 1000L
        private const val HISTORY_MAX_SIZE = 10
    }
}