package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.presentation.PlayerActivityViewModel
import com.example.playlistmaker.player.ui.model.PlayerActivityState
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    private lateinit var playerActivityViewModel : PlayerActivityViewModel

    private lateinit var btnBack: ImageButton
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnInFavorite: ImageButton
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

        playerActivityViewModel = ViewModelProvider(
            this,
            PlayerActivityViewModel.getViewModelFactory()
        )[PlayerActivityViewModel::class.java]

        btnBack = findViewById(R.id.ibBack)
        btnPlayPause = findViewById(R.id.ibPlay)
        btnInFavorite = findViewById(R.id.ibAddToFavorite)
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

// Кнопка Воспроизвести/Пауза
        btnPlayPause.setOnClickListener {
            playerActivityViewModel.playbackControl()
        }

// Кнопка назад
        btnBack.setOnClickListener {
            finish()
        }

// Кнопка добавить в избранное
        btnInFavorite.setOnClickListener {
            playerActivityViewModel.toggleFavorite()
        }

        playerActivityViewModel.onCreate(intent.getSerializableExtra(TRACK) as Track)
        playerActivityViewModel.observeState().observe(this) {
            render(it)
        }
        playerActivityViewModel.observeToastState().observe(this) { pairError ->
            showError(pairError)
        }
        playerActivityViewModel.observeAddInFavorite().observe(this) { inFavorite ->
            setInFavoriteImage(inFavorite)
        }
    }

    override fun onPause() {
        super.onPause()

        playerActivityViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        playerActivityViewModel.onDestroy()
    }

    private fun setTrackImage(artworkUrl512: String) {
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

    private fun render(state: PlayerActivityState) {
        when (state) {
            is PlayerActivityState.Prepare -> prepare(
                state.track,
                state.artworkUrl512,
                state.albumGroupIsVisible,
                state.countryGroupIsVisible,
                state.genreGroupIsVisible,
                state.releaseGroupIsVisible,
                state.trackTimeGroupIsVisible
            )
            is PlayerActivityState.FileNotFound -> fileNotFound()
            is PlayerActivityState.Play -> playTrack(state.isPlaying)
            is PlayerActivityState.Pause -> pauseTrack(state.isPaused)
            is PlayerActivityState.UpdateTimer -> updatePlayTime(state.time)
            is PlayerActivityState.Stop -> playerStop(state.isStoped)
        }
    }

    private fun fileNotFound() {
        enablePlayPause(false)
    }
    private fun playTrack(isPlaying: Boolean) {
        setPlayPause(isPlaying)
    }
    private fun pauseTrack(isPaused: Boolean) {
        setPlayPause(!isPaused)
    }
    private fun updatePlayTime(time: String) {
        setPlayTime(time)
    }
    private fun playerStop(isStoped:Boolean) {
        setPlayPause(!isStoped)
        setPlayTime(DEFAULT_TIME)
    }
    private fun prepare(
        track: Track,
        artworkUrl512: String,
        albumGroupIsVisible: Boolean,
        countryGroupIsVisible: Boolean,
        genreGroupIsVisible: Boolean,
        releaseGroupIsVisible: Boolean,
        trackTimeGroupIsVisible: Boolean
    ) {
        setTrackName(track.trackName)
        setArtistName(track.artistName)
        setTrackImage(artworkUrl512)
        setInFavoriteImage(track.inFavorite)
        setTrackTime(
            SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                .format(track.trackTimeMillis.toLong())
        )
        setAlbum(track.collectionName)
        setReleaseDate(playerActivityViewModel.getYear(track.releaseDate))
        setGenre(track.primaryGenreName)
        setCountry(track.country)
        showAlbumGroup(albumGroupIsVisible)
        showCountryGroup(countryGroupIsVisible)
        showGenreGroup(genreGroupIsVisible)
        showReleaseGroup(releaseGroupIsVisible)
        showTrackTimeGroup(trackTimeGroupIsVisible)
        enablePlayPause(true)
    }


    private fun setTrackName(trackName: String) {
        tvTrackName.text = trackName
    }
    private fun setAlbum(album: String) {
        tvAlbum.text = album
    }
    private fun setArtistName(artistName: String) {
        tvArtistName.text = artistName
    }
    private fun setPlayTime(playTime: String) {
        tvPlayTime.text = playTime
    }
    private fun setReleaseDate(releaseDate: String) {
        tvReleaseDate.text = releaseDate
    }
    private fun setTrackTime(trackTime: String) {
        tvTrackTime.text = trackTime
    }
    private fun setGenre(genre: String) {
        tvGenre.text = genre
    }
    private fun setCountry(country: String) {
        tvCountry.text = country
    }
    private fun enablePlayPause(isEnabled: Boolean) {
        btnPlayPause.isEnabled = isEnabled
    }
    private fun setPlayPause(isPlaying: Boolean) {
        btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_button_pause else R.drawable.ic_button_play
        )
    }
    private fun setInFavoriteImage(isFavorite: Boolean) {
        btnInFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_button_added_to_favorite else R.drawable.ic_button_add_to_favorite
        )
    }

    private fun showAlbumGroup(isVisible: Boolean) {
        albumGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showCountryGroup(isVisible: Boolean) {
        countryGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showGenreGroup(isVisible: Boolean) {
        genreGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showReleaseGroup(isVisible: Boolean) {
        releaseGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showTrackTimeGroup(isVisible: Boolean) {
        trackTimeGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showError(pairError: Pair<String, String>) {
        val error = pairError.first
        val errorMessage = pairError.second

        Toast.makeText(this, error+"\n"+errorMessage, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TRACK = "TRACK"
        private const val CORNERS_ANGLE = 8.0F
        private const val TIME_FORMAT = "m:ss"
        private const val DEFAULT_TIME = "0:00"
    }

}