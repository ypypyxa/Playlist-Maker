package com.example.playlistmaker.ui.playlists

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.playlistmaker.R

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backButton = findViewById<ImageButton>(R.id.ibBack)

        backButton.setOnClickListener {
            finish()
        }
    }
}