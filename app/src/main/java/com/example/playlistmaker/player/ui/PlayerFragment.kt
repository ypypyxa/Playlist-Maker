package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.model.PlayerActivityState
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding

    private val playerViewModel by viewModel<PlayerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка Воспроизвести/Пауза
        binding.btnPlayPause.setOnClickListener {
            playerViewModel.playbackControl()
        }

// Кнопка назад
        binding.btnBack.setOnClickListener {
            TODO("Ну точно не надо будет, но пока пусть будет так")
        }

// Кнопка добавить в избранное
        binding.btnInFavorite.setOnClickListener {
            playerViewModel.toggleFavorite()
        }

        playerViewModel.onCreate(requireArguments().getSerializable(ARGS_TRACK) as Track)
        playerViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        playerViewModel.observeToastState().observe(viewLifecycleOwner) { pairError ->
            showError(pairError)
        }
        playerViewModel.observeAddInFavorite().observe(viewLifecycleOwner) { inFavorite ->
            setInFavoriteImage(inFavorite)
        }
    }
    override fun onPause() {
        super.onPause()

        playerViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        playerViewModel.onDestroy()
    }

    private fun setTrackImage(artworkUrl512: String) {
        Glide.with(binding.tvTrackImage)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(binding.tvTrackImage.context)))
            .into(binding.tvTrackImage)
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
            is PlayerActivityState.FileNotFound -> {
                fileNotFound()
                prepare(
                    state.track,
                    state.artworkUrl512,
                    state.albumGroupIsVisible,
                    state.countryGroupIsVisible,
                    state.genreGroupIsVisible,
                    state.releaseGroupIsVisible,
                    state.trackTimeGroupIsVisible
                )
            }
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
        setReleaseDate(playerViewModel.getYear(track.releaseDate))
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
        binding.tvTrackName.text = trackName
    }
    private fun setAlbum(album: String) {
        binding.tvAlbum.text = album
    }
    private fun setArtistName(artistName: String) {
        binding.tvArtistName.text = artistName
    }
    private fun setPlayTime(playTime: String) {
        binding.tvPlayTime.text = playTime
    }
    private fun setReleaseDate(releaseDate: String) {
        binding.tvReleaseDate.text = releaseDate
    }
    private fun setTrackTime(trackTime: String) {
        binding.tvTrackTime.text = trackTime
    }
    private fun setGenre(genre: String) {
        binding.tvGenre.text = genre
    }
    private fun setCountry(country: String) {
        binding.tvCountry.text = country
    }
    private fun enablePlayPause(isEnabled: Boolean) {
        binding.btnPlayPause.isEnabled = isEnabled
    }
    private fun setPlayPause(isPlaying: Boolean) {
        binding.btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_button_pause else R.drawable.ic_button_play
        )
    }
    private fun setInFavoriteImage(isFavorite: Boolean) {
        binding.btnInFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_button_added_to_favorite else R.drawable.ic_button_add_to_favorite
        )
    }

    private fun showAlbumGroup(isVisible: Boolean) {
        binding.albumGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showCountryGroup(isVisible: Boolean) {
        binding.countryGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showGenreGroup(isVisible: Boolean) {
        binding.genreGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showReleaseGroup(isVisible: Boolean) {
        binding.releaseGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showTrackTimeGroup(isVisible: Boolean) {
        binding.trackTimeGroup.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showError(pairError: Pair<String, String>) {
        val error = pairError.first
        val errorMessage = pairError.second

        Toast.makeText(requireContext(), error+"\n"+errorMessage, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val CORNERS_ANGLE = 8.0F
        private const val TIME_FORMAT = "m:ss"
        private const val DEFAULT_TIME = "0:00"

        private const val ARGS_TRACK = "TRACK"

        // Тег для использования во FragmentManager
        const val TAG = "PlayerFragment"

        fun newInstance(track: Track): Fragment {
            return PlayerFragment().apply {
                // Пробрасываем аргументы в Bundle
                arguments = bundleOf(ARGS_TRACK to track)
            }
        }
    }
}