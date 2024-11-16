package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerRepositoryImpl : MediaPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = PlayerState.STATE_DEFAULT

    // Метод для инициализации MediaPlayer один раз
    private fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setOnPreparedListener {
                playerState = PlayerState.STATE_PREPARED
            }
            mediaPlayer?.setOnCompletionListener {
                playerState = PlayerState.STATE_COMPLETE
            }
        }
    }

    override fun prepare(url: String) {
        initMediaPlayer()
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepareAsync()
    }

    override fun start() {
        if (playerState == PlayerState.STATE_PREPARED || playerState == PlayerState.STATE_PAUSED) {
            mediaPlayer?.start()
            playerState = PlayerState.STATE_PLAYING
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = PlayerState.STATE_DEFAULT
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            .format(mediaPlayer?.currentPosition)
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun getCurrentPositionMillis(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    companion object {
        private const val TIME_FORMAT = "m:ss"
    }
}