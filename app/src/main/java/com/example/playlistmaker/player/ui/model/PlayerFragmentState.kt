package com.example.playlistmaker.player.ui.model

import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track

interface PlayerFragmentState {

    data class Prepare(
        val track: Track,
        val artworkUrl512: String,
        val albumGroupIsVisible: Boolean,
        val countryGroupIsVisible: Boolean,
        val genreGroupIsVisible: Boolean,
        val releaseGroupIsVisible: Boolean,
        val trackTimeGroupIsVisible: Boolean
    ) : PlayerFragmentState

    // Состояние экрана, когда воспроизведение не возможно
    data class FileNotFound(
        val track: Track,
        val artworkUrl512: String,
        val albumGroupIsVisible: Boolean,
        val countryGroupIsVisible: Boolean,
        val genreGroupIsVisible: Boolean,
        val releaseGroupIsVisible: Boolean,
        val trackTimeGroupIsVisible: Boolean
    ) : PlayerFragmentState

    // Состояние экрана во время проигрывания
    data class Play(
        val isPlaying: Boolean
    ) : PlayerFragmentState

    // Состояние экрана во время паузы
    data class Pause(
        val isPaused: Boolean
    ) : PlayerFragmentState

    data class Stop(
        val isStoped: Boolean
    ) : PlayerFragmentState

    //Обновление таймера
    data class UpdateTimer(
        val time: String
    ) : PlayerFragmentState
}