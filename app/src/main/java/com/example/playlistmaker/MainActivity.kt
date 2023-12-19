package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        val buttonClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Toast.makeText(this@MainActivity, "Тут пока ни чего нет", Toast.LENGTH_SHORT).show()
            }
        }

        searchButton.setOnClickListener(buttonClickListener)
        mediaButton.setOnClickListener(buttonClickListener)

        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Настройки видимо будут одним из следующих уроков", Toast.LENGTH_SHORT).show()
        }
    }
}