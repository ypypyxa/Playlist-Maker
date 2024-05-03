package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface MediaPlayerManager {
    fun prepare(url: String)
    fun start()
    fun pause()
    fun release()
    fun getCurentPosition(): String
    fun getPlayerState(): PlayerState
}