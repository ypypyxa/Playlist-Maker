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

        val track: Track = intent.getSerializableExtra("TRACK") as Track
        val artworkUrl512 = track.artworkUrl100.replaceAfterLast('/', BIG_SIZE+".jpg")

        when {
            track.collectionName == "" -> albumGroup.visibility = View.GONE
            track.collectionName != "" -> albumGroup.visibility = View.VISIBLE
            track.releaseDate == "" -> releaseGroup.visibility = View.GONE
            track.releaseDate != "" -> releaseGroup.visibility = View.VISIBLE
            track.primaryGenreName == "" -> genreGroup.visibility = View.GONE
            track.primaryGenreName != "" -> genreGroup.visibility = View.VISIBLE
            track.country == "" -> countryGroup.visibility = View.GONE
            track.country != "" -> countryGroup.visibility = View.VISIBLE
        }

        tvAlbum.text = track.collectionName
        tvArtistName.text = track.artistName
        tvTrackName.text = track.trackName
        tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        tvReleaseDate.text = track.releaseDate
        tvReleaseDate.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(track.releaseDate))
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        Glide.with(tvTrackImage)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(8.0F, tvTrackImage.context)))
            .into(tvTrackImage)

//Кнопка назад
        btnBack.setOnClickListener {
            finish()
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
    companion object {
        private const val BIG_SIZE = "512x512"
    }
}
