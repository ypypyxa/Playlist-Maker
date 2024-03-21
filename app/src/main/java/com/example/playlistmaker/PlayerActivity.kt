package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var tvAlbum: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvTrackName: TextView
    private lateinit var tvTrackTime: TextView
    private lateinit var tvTrackImage: ImageView
    private lateinit var tvReleaseDate: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvCountry: TextView
    private lateinit var albumGroup: Group
    private lateinit var countryGroup: Group
    private lateinit var genreGroup: Group
    private lateinit var releaseGroup: Group
    private lateinit var trackTimeGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        btnBack = findViewById(R.id.btnBack)
        tvAlbum = findViewById(R.id.tvAlbum)
        tvArtistName = findViewById(R.id.tvArtistName)
        tvTrackName = findViewById(R.id.tvTrackName)
        tvTrackTime = findViewById(R.id.tvTrackTime)
        tvTrackImage = findViewById(R.id.ivTrackImage)
        tvReleaseDate = findViewById(R.id.tvRelease)
        tvGenre = findViewById(R.id.tvGenre)
        tvCountry = findViewById(R.id.tvCountry)
        albumGroup = findViewById(R.id.albumGroup)
        countryGroup = findViewById(R.id.countryGroup)
        genreGroup = findViewById(R.id.genreGroup)
        releaseGroup = findViewById(R.id.releaseGroup)
        trackTimeGroup = findViewById(R.id.trackTimeGroup)

        val track: Track = intent.getSerializableExtra(TRACK) as Track
        val artworkUrl512 = track.artworkUrl100.replaceAfterLast(DELIMITER, "$BIG_SIZE.jpg")

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvTrackTime.text = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            .format(track.trackTimeMillis.toLong())
        if (track.collectionName.isNotEmpty()) {
            tvAlbum.text = track.collectionName
            albumGroup.visibility = View.VISIBLE
        } else {
            albumGroup.visibility = View.GONE
        }
        if (track.releaseDate.isNotEmpty()) {
            tvReleaseDate.text = getYear(track.releaseDate)
            releaseGroup.visibility = View.VISIBLE
        } else {
            releaseGroup.visibility = View.GONE
        }
        if (track.primaryGenreName.isNotEmpty()) {
            tvGenre.text = track.primaryGenreName
            genreGroup.visibility = View.VISIBLE
        } else {
            genreGroup.visibility = View.GONE
        }
        if (track.country.isNotEmpty()) {
            tvCountry.text = track.country
            countryGroup.visibility = View.VISIBLE
        } else {
            countryGroup.visibility = View.GONE
        }


        Glide.with(tvTrackImage)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(tvTrackImage.context)))
            .into(tvTrackImage)

//Кнопка назад
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            CORNERS_ANGLE,
            context.resources.displayMetrics).toInt()
    }

    private fun getYear(date: String) : String {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).parse(date)!!
        return calendar.get(Calendar.YEAR).toString()
    }
    companion object {
        private const val BIG_SIZE = "512x512"
        private const val CORNERS_ANGLE = 8.0F
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private const val DELIMITER = '/'
        private const val TRACK = "TRACK"
        private const val TIME_FORMAT = "mm:ss"
    }
}