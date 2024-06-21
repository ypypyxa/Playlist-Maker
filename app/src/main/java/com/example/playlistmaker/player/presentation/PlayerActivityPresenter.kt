package com.example.playlistmaker.player.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerActivityPresenter(
    private val playerView: PlayerView,
    private val context: Context
) {

    private lateinit var mediaPlayer: MediaPlayerInteractor
    private lateinit var track: Track

    // Объявление перменных для таймера воспроизведения
    private lateinit var handler: Handler
    private val updateTime = object : Runnable {
        override fun run() {
            playerView.setPlayTime(mediaPlayer.getCurrentPosition())
            playerState = mediaPlayer.getPlayerState()
            if (playerState == PlayerState.STATE_COMPLETE) {
                playerView.setPlayPause(isPlaying = false)
                playerState = PlayerState.STATE_PREPARED
                playerView.setPlayTime(DEFAULT_TIME)
                handler.removeCallbacks(this)
            }
            handler.postDelayed(this, DELAY) // Обновляем каждую секунду
        }
    }

    private var playerState = PlayerState.STATE_DEFAULT

    fun onCreate(recivdeTrack: Track) {
        this.track = recivdeTrack

// Лучшего места что бы сохранить трек в историю поиска я пока не нашел...
        val historyInteractor = Creator.provideHistoryInteractor(context)
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

        playerView.setTrackName(track.trackName)
        playerView.setArtistName(track.artistName)
        playerView.setTrackImage(artworkUrl512)
        playerView.setTrackTime(
            SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                .format(track.trackTimeMillis.toLong())
        )
        if (track.collectionName.isNotEmpty()) {
            playerView.setAlbum(track.collectionName)
            playerView.showAlbumGroup(true)
        } else {
            playerView.showAlbumGroup(false)
        }
        if (track.releaseDate.isNotEmpty()) {
            playerView.setReleaseDate(getYear(track.releaseDate))
            playerView.showReleaseGroup(true)
        } else {
            playerView.showReleaseGroup(false)
        }
        if (track.primaryGenreName.isNotEmpty()) {
            playerView.setGenre(track.primaryGenreName)
            playerView.showGenreGroup(true)
        } else {
            playerView.showGenreGroup(false)
        }
        if (track.country.isNotEmpty()) {
            playerView.setCountry(track.country)
            playerView.showCountryGroup(true)
        } else {
            playerView.showCountryGroup(false)
        }

// Подготавливаем плеер
        preparePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onDestroy() {
        handler.removeCallbacks(updateTime)
        mediaPlayer.release()
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
            playerView.enablePlayPause(true)
        } else {
            showMessage(context.getString(R.string.player_error),context.getString(R.string.empty_track_url))
            playerView.enablePlayPause(false)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerView.setPlayPause(isPlaying = true)
        playerState = PlayerState.STATE_PLAYING
        handler.post(updateTime)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerView.setPlayPause(isPlaying = false)
        playerState = PlayerState.STATE_PAUSED
        handler.removeCallbacks(updateTime)
    }

    private fun getYear(date: String) : String {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).parse(date)!!
        return calendar.get(Calendar.YEAR).toString()
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(context, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        private const val BIG_SIZE = "512x512"
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private const val DELIMITER = '/'
        private const val DELAY = 1000L
        private const val DEFAULT_TIME = "0:00"
        private const val TIME_FORMAT = "m:ss"
        private const val HISTORY = "history"
        private const val HISTORY_MIN_SIZE = 0
        private const val HISTORY_MAX_SIZE = 10
    }
}