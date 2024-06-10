package com.example.playlistmaker.domain.models

import java.io.Serializable

enum class PlayerState {
    STATE_DEFAULT,
    STATE_PREPARED,
    STATE_PLAYING,
    STATE_PAUSED,
    STATE_COMPLETE
}