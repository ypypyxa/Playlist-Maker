package com.example.playlistmaker.player.domain.Impl

import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) : MediaPlayerInteractor {
    override fun prepare(url: String) {
        mediaPlayerRepository.prepare(url)
    }

    override fun start() {
        mediaPlayerRepository.start()
    }

    override fun pause() {
        mediaPlayerRepository.pause()
    }

    override fun release() {
        mediaPlayerRepository.release()
    }

    override fun getCurrentPosition(): String {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return mediaPlayerRepository.getPlayerState()
    }

    override fun seekTo(position: Int) {
        mediaPlayerRepository.seekTo(position)
    }

    override fun getCurrentPositionMillis(): Int {
        return mediaPlayerRepository.getCurrentPositionMillis()
    }
}