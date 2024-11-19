package com.example.playlistmaker.media.playlist.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.ui.adapters.TrackListAdapter
import com.example.playlistmaker.common.utils.DurationUtils
import com.example.playlistmaker.common.utils.TrackWordUtils
import com.example.playlistmaker.common.utils.gone
import com.example.playlistmaker.common.utils.show
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.playlist.ui.model.PlaylistFragmentState
import com.example.playlistmaker.player.ui.PlayerFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlist: Playlist

    private var isClickAllowed = true

    private val playlistViewModel by viewModel<PlaylistViewModel>()

    private var onTrackClickDebounce: ((Track) -> Unit)? = null
    private var onLongTrackClickDebounce: ((Track) -> Unit)? = null

    private lateinit var trackList: RecyclerView

    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var extendedBottomSheet: BottomSheetBehavior<LinearLayout>

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getSerializable(ARGS_PLAYLIST) as Playlist

        // Инициализация BottomSheetBehavior
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Получаем невидимый View
        val sharebutton = view.findViewById<ImageView>(R.id.shareButton)

///////////////////////////////вынести в функцию/////////////////////////////////////////////////////

        // Используем ViewTreeObserver для получения позиции positionMarker
        sharebutton.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Получаем Y-координату positionMarker относительно экрана
                val location = IntArray(2)
                sharebutton.getLocationOnScreen(location)
                val positionMarkerY = location[1]// + sharebutton.height

                // Получаем высоту экрана
                val screenHeight = requireView().height //Resources.getSystem().displayMetrics.heightPixels

                // Вычисляем высоту BottomSheet
                val bottomSheetPeekHeight = screenHeight - positionMarkerY

                // Устанавливаем peekHeight
                bottomSheetBehavior.peekHeight = bottomSheetPeekHeight

                // Удаляем слушатель, чтобы предотвратить повторные вызовы
                sharebutton.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

/////////////////////////////////////////////////////////////////////////////////////////////////////

        extendedBottomSheet = BottomSheetBehavior.from(binding.extendedMenuBottomSheet)
        extendedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        trackListAdapter = TrackListAdapter { item ->
            // Нажатие на итем
            if (clickDebounce()) {
                // Навигируемся на следующий экран
                findNavController().navigate(
                    R.id.action_playlistFragment_to_playerFragment,
                    PlayerFragment.createArgs(item)
                )
            }
        }

        trackList = binding.tracksInPlaylistRecyclerView

        trackList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackList.adapter = trackListAdapter

        playlistViewModel.fillData()
        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()

        isClickAllowed = true
    }

    private fun render(state: PlaylistFragmentState) {
        when (state) {
            is PlaylistFragmentState.Content -> showContent()
        }
    }

    private fun showContent() {
        setTrackImage(playlist.artworkUri.toString())
        binding.playlistName.text = playlist.playlistName
        binding.playlistDescription.text = playlist.playlistDescription
        binding.playlistDuration.text = DurationUtils.getTotalMinutes(playlist.tracks)
        binding.tracksCountPlaylist.text = TrackWordUtils(requireContext()).getTrackWord(playlist.tracksCount)

        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(playlist.tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun setTrackImage(artworkUrl512: String) {
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .into(binding.playlistCover)
    }

    // Задержка между кликами
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        const val ARGS_PLAYLIST = "PLAYLIST"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(ARGS_PLAYLIST to playlist)
    }
}