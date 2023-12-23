package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.back)
        val mailToSupButton = findViewById<ImageButton>(R.id.mail_to_support)

        backButton.setOnClickListener {
            finish()
        }

        mailToSupButton.setOnClickListener {
            val mailToSupIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("axypypy@yandex.ru"))
                putExtra(Intent.EXTRA_SUBJECT, "Проблема с приложением Playlist Maker")
                putExtra(Intent.EXTRA_TEXT, "Помогите разобраться с проблемой")
            }
            startActivity(mailToSupIntent)
        }
    }
}