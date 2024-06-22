package com.example.playlistmaker.player.ui.model

import com.example.playlistmaker.search.domain.model.Track

interface PlayerActivityState {

    data class Prepare(
        val track: Track,
        val artworkUrl512: String,
        val albumGroupIsVisible: Boolean,
        val countryGroupIsVisible: Boolean,
        val genreGroupIsVisible: Boolean,
        val releaseGroupIsVisible: Boolean,
        val trackTimeGroupIsVisible: Boolean
    ) : PlayerActivityState

    // Состояние экрана, когда воспроизведение не возможно
    data object FileNotFound : PlayerActivityState

    // Состояние экрана во время проигрывания
    data class Play(
        val isPlaying: Boolean
    ) : PlayerActivityState

    // Состояние экрана во время паузы
    data class Pause(
        val isPaused: Boolean
    ) : PlayerActivityState

    data class Stop(
        val isStoped: Boolean
    ) : PlayerActivityState

    //Обновление таймера
    data class UpdateTimer(
        val time: String
    ) : PlayerActivityState
}