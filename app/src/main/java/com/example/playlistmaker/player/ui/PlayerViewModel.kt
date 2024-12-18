package com.example.playlistmaker.player.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.media.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.ui.model.PlayerFragmentState
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.domain.api.HistoryInteractor
import com.example.playlistmaker.common.utils.SingleLiveEvent
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerViewModel(
    private val historyInteractor: HistoryInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
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

    private var currentPosition: Int = 0

    private val playerLiveData = MutableLiveData<PlayerFragmentState>()
    fun observeState(): LiveData<PlayerFragmentState> = playerLiveData

    private val showError = SingleLiveEvent<Pair<String, String>>()
    fun observeToastState(): LiveData<Pair<String, String>> = showError

    private val addInFavorite = SingleLiveEvent<Boolean>()
    fun observeAddInFavorite(): LiveData<Boolean> = addInFavorite

    private val _alpha = MutableLiveData<Float>()
    val alpha: LiveData<Float> get() = _alpha

    private val playlists = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlists

    private val showAddToPlaylistResult = SingleLiveEvent<String>()
    fun observeAddToPlaylistResult(): LiveData<String> = showAddToPlaylistResult

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

    fun onResume() {
        if (playerState == PlayerState.STATE_PAUSED) {
            renderState(
                PlayerFragmentState.Pause(true)
            )
            renderState(
                PlayerFragmentState.UpdateTimer(
                    SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                        .format(currentPosition)
                )
            )
        }
    }

    fun onPause() {
        currentPosition = mediaPlayer.getCurrentPositionMillis()
        pausePlayer()
        timerJob?.cancel()
    }

    fun onDestroy() {
        timerJob?.cancel()
        mediaPlayer.release()
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
        if (currentPosition > 0) {
            mediaPlayer.seekTo(currentPosition)
        }

        mediaPlayer.start()
        renderState(
            PlayerFragmentState.Play(true)
        )
        playerState = PlayerState.STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        currentPosition = mediaPlayer.getCurrentPositionMillis()
        mediaPlayer.pause()
        renderState(
            PlayerFragmentState.Pause(true)
        )
        playerState = PlayerState.STATE_PAUSED
        timerJob?.cancel()
    }

    private fun stopPlayer() {
        currentPosition = 0
        mediaPlayer.release()

        renderState(
            PlayerFragmentState.Stop(true)
        )
        preparePlayer()
        playerState = PlayerState.STATE_PREPARED
        timerJob?.cancel()
    }

    fun getYear(date: String) : String {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).parse(date)!!
        return calendar.get(Calendar.YEAR).toString()
    }

    fun updateAlpha(slideOffset: Float) {
        val maxAlpha = 3f // Максимальное значение альфы (менее прозрачное)
        val adjustedAlpha = ((slideOffset + 1) / 2) * maxAlpha
        _alpha.value = adjustedAlpha
    }

    fun updateList() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect() {
                playlists.postValue(it)
            }
        }
    }

    fun addToPlaylist(playlist: Playlist) {
        val trackExists = playlist.tracks.any { it.trackId == track.trackId }
        if (trackExists) {
            // Трек уже есть в плейлисте
            val message = getApplication<Application>().getString(R.string.already_added)
            showAddToPlaylistResult.postValue("$message ${playlist.playlistName}")
        } else {
            viewModelScope.launch {
                // Добавляем трек в плейлист
                val message = getApplication<Application>().getString(R.string.added_to_playlist)
                playlistInteractor.addTrackToPlaylist(playlist.playlistId, track)
                showAddToPlaylistResult.postValue("$message ${playlist.playlistName}")
            }
        }
    }

    companion object {
        private const val BIG_SIZE = "512x512"
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private const val DELIMITER = '/'
        private const val DELAY = 300L
        private const val TIME_FORMAT = "m:ss"
    }
}