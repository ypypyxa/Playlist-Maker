package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.ibBack)
        val mailToSupButton = findViewById<ImageButton>(R.id.ibMailToSupport)
        val shareButton = findViewById<ImageButton>(R.id.ibShare)
        val agreementButton = findViewById<ImageButton>(R.id.ibUserAgreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.swTheme)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val title = getString(R.string.share_title)
            val url = getString(R.string.yp_url)
            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.putExtra(Intent.EXTRA_TEXT, url)
            shareIntent.type = "aplication/octet-stream"
            startActivity(Intent.createChooser(shareIntent, title))
        }

        mailToSupButton.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                val supportEmail = getString(R.string.support_email)
                val messageSubject = getString(R.string.message_subject_to_support)
                val message = getString(R.string.message_to_support)

                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
                putExtra(Intent.EXTRA_SUBJECT, messageSubject)
                putExtra(Intent.EXTRA_TEXT, message)
                startActivity(this)
            }
        }

        agreementButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.offer_url))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)

            startActivity(agreementIntent)
        }
    }
}