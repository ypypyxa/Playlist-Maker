package com.example.playlistmaker.player.ui

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
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.player.presentation.PlayerActivityPresenter
import com.example.playlistmaker.player.presentation.PlayerView
import com.example.playlistmaker.search.domain.model.Track

class PlayerActivity : AppCompatActivity(), PlayerView {

    private val playerActivityPresenter = Creator.providePlayerActivityPresenter(this, this)

    private lateinit var btnBack: ImageButton
    private lateinit var btnPlayPause: ImageButton
    private lateinit var tvAlbum: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvPlayTime: TextView
    private lateinit var tvReleaseDate: TextView
    private lateinit var tvTrackName: TextView
    private lateinit var tvTrackTime: TextView
    private lateinit var tvTrackImage: ImageView
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

        btnBack = findViewById(R.id.ibBack)
        btnPlayPause = findViewById(R.id.ibPlay)
        tvAlbum = findViewById(R.id.tvAlbum)
        tvArtistName = findViewById(R.id.tvArtistName)
        tvPlayTime = findViewById((R.id.tvPlayTime))
        tvReleaseDate = findViewById(R.id.tvRelease)
        tvTrackName = findViewById(R.id.tvTrackName)
        tvTrackTime = findViewById(R.id.tvTrackTime)
        tvTrackImage = findViewById(R.id.ivTrackImage)
        tvGenre = findViewById(R.id.tvGenre)
        tvCountry = findViewById(R.id.tvCountry)
        albumGroup = findViewById(R.id.albumGroup)
        countryGroup = findViewById(R.id.countryGroup)
        genreGroup = findViewById(R.id.genreGroup)
        releaseGroup = findViewById(R.id.releaseGroup)
        trackTimeGroup = findViewById(R.id.trackTimeGroup)

        playerActivityPresenter.onCreate(intent.getSerializableExtra(TRACK) as Track)

// Кнопка Воспроизвести/Пауза
        btnPlayPause.setOnClickListener {
            playerActivityPresenter.playbackControl()
        }

// Кнопка назад
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()

        playerActivityPresenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        playerActivityPresenter.onDestroy()
    }

    override fun setTrackImage(artworkUrl512: String) {
        Glide.with(tvTrackImage)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(tvTrackImage.context)))
            .into(tvTrackImage)
    }

    private fun dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            CORNERS_ANGLE,
            context.resources.displayMetrics).toInt()
    }

    override fun setTrackName(trackName: String) {
        tvTrackName.text = trackName
    }
    override fun setAlbum(album: String) {
        tvAlbum.text = album
    }
    override fun setArtistName(artistName: String) {
        tvArtistName.text = artistName
    }
    override fun setPlayTime(playTime: String) {
        tvPlayTime.text = playTime
    }
    override fun setReleaseDate(releaseDate: String) {
        tvReleaseDate.text = releaseDate
    }
    override fun setTrackTime(trackTime: String) {
        tvTrackTime.text = trackTime
    }
    override fun setGenre(genre: String) {
        tvGenre.text = genre
    }
    override fun setCountry(country: String) {
        tvCountry.text = country
    }

    override fun enablePlayPause(isEnabled: Boolean) {
        btnPlayPause.isEnabled = isEnabled
    }
    override fun setPlayPause(isPlaying: Boolean) {
        btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_button_pause else R.drawable.ic_button_play
        )
    }

    override fun showAlbumGroup(isVisible: Boolean) {
        albumGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showCountryGroup(isVisible: Boolean) {
        countryGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showGenreGroup(isVisible: Boolean) {
        genreGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showReleaseGroup(isVisible: Boolean) {
        releaseGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showTrackTimeGroup(isVisible: Boolean) {
        trackTimeGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TRACK = "TRACK"
        private const val CORNERS_ANGLE = 8.0F
    }

}