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
        val shareButton = findViewById<ImageButton>(R.id.share)
        val agreementButton = findViewById<ImageButton>(R.id.user_agreement)

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val title = "Выберите приложение"
            val url = "https://practicum.yandex.ru/android-developer/"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)
            shareIntent.type = "aplication/octet-stream"
            startActivity(Intent.createChooser(shareIntent, title))
        }

        mailToSupButton.setOnClickListener {
            val mailToSupIntent = Intent(Intent.ACTION_SENDTO).apply {
                val supportEmail = "axypypy@yandex.ru"
                val messageSubject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
                val message = "Спасибо за крутое приложение!"

                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
                putExtra(Intent.EXTRA_SUBJECT, messageSubject)
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(mailToSupIntent)
        }

        agreementButton.setOnClickListener {
            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)

            startActivity(agreementIntent)
        }
    }
}