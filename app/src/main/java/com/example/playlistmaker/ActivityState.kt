package com.example.playlistmaker

import java.io.Serializable

enum class ActivityState : Serializable {
    FAILURE, NOTHING_FOUND, SHOW_SEARCH_LIST, SHOW_HISTORY_LIST, SHOW_NOTHING
}