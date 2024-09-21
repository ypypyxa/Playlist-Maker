package com.example.playlistmaker.utils

import android.view.View

/**
 * Делает View видимым
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Делает View полностью скрытым и удаляет его место в макете
 */
fun View.gone() {
    this.visibility = View.GONE
}