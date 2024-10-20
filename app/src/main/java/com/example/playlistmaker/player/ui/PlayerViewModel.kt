package com.example.playlistmaker.player.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.favorites.domain.db.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.model.PlayerFragmentState
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.common.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerViewModel(
    private val historyInteractor: HistoryInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val mediaPlayer: MediaPlayerInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private lateinit var track: Track
    private lateinit var artworkUrl512: String
    private var albumGroupIsVisible = false
    private var countryGroupIsVisible = false
    private var genreGroupIsVisible = false
    private var releaseGroupIsVisible = false
    private var trackTimeGroupIsVisible = false

    private var timerJob: Job? = null

    private val playerLiveData = MutableLiveData<PlayerFragmentState>()
    fun observeState(): LiveData<PlayerFragmentState> = playerLiveData

    private val showError = SingleLiveEvent<Pair<String, String>>()
    fun observeToastState(): LiveData<Pair<String, String>> = showError

    private val addInFavorite = SingleLiveEvent<Boolean>()
    fun observeAddInFavorite(): LiveData<Boolean> = addInFavorite

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.STATE_PLAYING) {
                delay(DELAY)
                renderState(
                    PlayerFragmentState.UpdateTimer(
                        mediaPlayer.getCurrentPosition()
                    )
                )
                playerState = mediaPlayer.getPlayerState()
                if (playerState == PlayerState.STATE_COMPLETE) {
                    stopPlayer()
                }
            }
        }
    }

    private var playerState = PlayerState.STATE_DEFAULT

    fun onCreate(receivedTrack: Track) {
        this.track = receivedTrack

        artworkUrl512 = track.artworkUrl100.replaceAfterLast(DELIMITER, "$BIG_SIZE.jpg")

        albumGroupIsVisible = track.collectionName.isNotEmpty()
        countryGroupIsVisible = track.releaseDate.isNotEmpty()
        genreGroupIsVisible = track.primaryGenreName.isNotEmpty()
        releaseGroupIsVisible = track.country.isNotEmpty()
        trackTimeGroupIsVisible = track.previewUrl.isNotEmpty()

        renderState(
            PlayerFragmentState.Prepare(
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
        timerJob?.cancel()
    }

    fun onDestroy() {
        timerJob?.cancel()
        mediaPlayer.release()
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayer()
    }

    private fun renderState(state: PlayerFragmentState) {
        playerLiveData.postValue(state)
    }

    fun toggleFavorite() {
        if (track.inFavorite) {
            track = track.copy(
                inFavorite = false,
                addToFavoritesDate = 0
            )
            viewModelScope.launch {
                favoritesInteractor.removeFromFavorites(track)
            }
            addInFavorite.postValue(false)
        } else {
            track = track.copy(
                inFavorite = true,
                addToFavoritesDate = System.currentTimeMillis()
            )
            viewModelScope.launch {
                favoritesInteractor.addToFavorites(track)
            }
            addInFavorite.postValue(true)
        }
        historyInteractor.updateHistoryList(track)
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
                PlayerFragmentState.FileNotFound(
                    track,
                    artworkUrl512,
                    albumGroupIsVisible,
                    countryGroupIsVisible,
                    genreGroupIsVisible,
                    releaseGroupIsVisible,
                    trackTimeGroupIsVisible
                )
            )
            val error = getApplication<Application>().getString(R.string.player_error)
            val errorMessage = getApplication<Application>().getString(R.string.empty_track_url)
            showError.postValue(Pair(error, errorMessage))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        renderState(
            PlayerFragmentState.Play(true)
        )
        playerState = PlayerState.STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        renderState(
            PlayerFragmentState.Pause(true)
        )
        playerState = PlayerState.STATE_PAUSED
        timerJob?.cancel()
    }

    private fun stopPlayer() {
        renderState(
            PlayerFragmentState.Stop(true)
        )
        playerState = PlayerState.STATE_PREPARED
        timerJob?.cancel()
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
        private const val DELAY = 300L
    }
}