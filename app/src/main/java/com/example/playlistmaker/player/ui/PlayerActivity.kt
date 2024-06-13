package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R

class PlayerActivity : AppCompatActivity() {

    private val playerActivityController = Creator.providePlayerActivityController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerActivityController.onCreate()
    }

    override fun onPause() {
        super.onPause()

        playerActivityController.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        playerActivityController.onDestroy()
    }

}