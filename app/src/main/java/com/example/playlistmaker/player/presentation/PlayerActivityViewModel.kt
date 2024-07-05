package com.example.playlistmaker.player.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.model.PlayerActivityState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.SingleLiveEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerActivityViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var mediaPlayer: MediaPlayerInteractor
    private lateinit var track: Track

    private val historyInteractor = Creator.provideHistoryInteractor(getApplication())

    private val playerActivityLiveData = MutableLiveData<PlayerActivityState>()
    fun observeState(): LiveData<PlayerActivityState> = playerActivityLiveData

    private val showError = SingleLiveEvent<Pair<String, String>>()
    fun observeToastState(): LiveData<Pair<String, String>> = showError

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
        val historyTracks = historyInteractor.loadTracks()
        if (historyTracks.none { it.trackId == track.trackId }) {
            historyTracks.add(0, track)
        } else {
            historyTracks.remove(track)
            historyTracks.add(0, track)
        }
        if (historyTracks.size > HISTORY_MAX_SIZE) {
            historyTracks.removeAt(HISTORY_MAX_SIZE)
        }
        historyInteractor.saveTracks(historyTracks)

        handler = Handler(Looper.getMainLooper())

        val artworkUrl512 = track.artworkUrl100.replaceAfterLast(DELIMITER, "${BIG_SIZE}.jpg")

        mediaPlayer = Creator.provideMediaPlayer()

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

    private fun renderState(state: PlayerActivityState) {
        playerActivityLiveData.postValue(state)
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

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerActivityViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}