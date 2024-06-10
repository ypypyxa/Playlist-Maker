package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

class MediaPlayerInteractor(private val mediaPlayerRepository: MediaPlayerRepository) {
    fun prepare(url: String) {
        mediaPlayerRepository.prepare(url)
    }

    fun start() {
        mediaPlayerRepository.start()
    }

    fun pause() {
        mediaPlayerRepository.pause()
    }

    fun release() {
        mediaPlayerRepository.release()
    }

    fun getCurrentPosition(): String {
        return mediaPlayerRepository.getCurrentPosition()
    }

    fun getPlayerState(): PlayerState {
        return mediaPlayerRepository.getPlayerState()
    }
}