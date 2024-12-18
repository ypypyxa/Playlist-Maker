package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface MediaPlayerInteractor {
    fun prepare(url: String)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): String
    fun getPlayerState(): PlayerState

    fun getCurrentPositionMillis(): Int
    fun seekTo(position: Int)
}