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
                val supportEmail = "axypypy@yandex.ru"
                val messageSubject = "Проблема с приложением Playlist Maker"
                val message = "Помогите разобраться с проблемой"

                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
                putExtra(Intent.EXTRA_SUBJECT, messageSubject)
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(mailToSupIntent)
        }
    }
}