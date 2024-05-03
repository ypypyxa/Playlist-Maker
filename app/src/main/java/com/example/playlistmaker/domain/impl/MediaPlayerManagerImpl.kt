package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerManager
import com.example.playlistmaker.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerManagerImpl : MediaPlayerManager {

    private val mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT

    override fun prepare(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = PlayerState.STATE_PREPARED
            }
            setOnCompletionListener {
                playerState = PlayerState.STATE_COMPLETE
            }
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurentPosition(): String {
        return SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }
    companion object {
        private const val TIME_FORMAT = "m:ss"
    }
}