package com.example.playlistmaker.media.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
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
import com.example.playlistmaker.common.utils.DurationUtils
import com.example.playlistmaker.common.utils.TrackWordUtils
import com.example.playlistmaker.common.utils.show
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.edit.ui.EditPlaylistFragment
import com.example.playlistmaker.media.playlist.ui.model.PlaylistFragmentState
import com.example.playlistmaker.player.ui.PlayerFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlist: Playlist

    private var isClickAllowed = true

    private val playlistViewModel by viewModel<PlaylistViewModel>()

    private lateinit var trackList: RecyclerView

    private lateinit var trackListAdapter: TracksOnPlaylistAdapter
    private lateinit var extendedBottomSheet: BottomSheetBehavior<LinearLayout>

    private lateinit var tracksBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var shareButton: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getSerializable(ARGS_PLAYLIST) as Playlist
        parentFragmentManager.setFragmentResultListener("playlistUpdated", viewLifecycleOwner) { key, bundle ->
            val playlistId = bundle.getLong("playlistId")
            playlistViewModel.refreshPlaylist(playlistId)
        }

        shareButton = binding.shareButton

        setTracksBehaviorHeight()

        extendedBottomSheet = BottomSheetBehavior.from(binding.extendedMenuBottomSheet)
        extendedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        extendedBottomSheet.peekHeight = (resources.displayMetrics.heightPixels * 2) / 3

        extendedBottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {

//                        overlay.visibility = View.GONE
                    }
                    else -> {
//                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                playerViewModel.updateAlpha(slideOffset)
            }
        })

        trackListAdapter = TracksOnPlaylistAdapter(
            onItemClickListener = { item ->
                // Нажатие на элемент
                if (clickDebounce()) {
                    findNavController().navigate(
                        R.id.action_playlistFragment_to_playerFragment,
                        PlayerFragment.createArgs(item)
                    )
                }
            },
            onItemLongClickListener = { item ->
                // Долгое нажатие на элемент
                showDeleteTrackConfirmationDialog(item)
            }
        )

        trackList = binding.tracksInPlaylistRecyclerView

        trackList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackList.adapter = trackListAdapter

        playlistViewModel.fillData()

        setListeners()

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        if (playlist.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()

        isClickAllowed = true
    }

    private fun render(state: PlaylistFragmentState) {
        when (state) {
            is PlaylistFragmentState.Content -> showContent()
            is PlaylistFragmentState.RefreshContent -> refreshContent(state.playlist)
        }
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener() {
            findNavController().popBackStack()
        }

        shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.extendedMenu.setOnClickListener {
            extendedBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.extendedMenuShareButton.setOnClickListener {
            sharePlaylist()
        }
        binding.extendedMenuDeleteButton.setOnClickListener {
            showDeletePlaylistConfirmationDialog()
        }
        binding.extendedMenuEditButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlist)
            )
        }
    }

    private fun setTracksBehaviorHeight() {
        val bottomSheet = binding.bottomSheet
        tracksBehavior = BottomSheetBehavior.from(bottomSheet)

        // Используем ViewTreeObserver для получения позиции positionMarker
        shareButton.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Получаем Y-координату positionMarker относительно экрана
                val location = IntArray(2)
                shareButton.getLocationOnScreen(location)
                val positionMarkerY = location[1] + 16

                // Получаем высоту экрана
                val screenHeight = requireView().height

                // Вычисляем высоту BottomSheet
                val bottomSheetPeekHeight = screenHeight - positionMarkerY

                // Устанавливаем peekHeight
                tracksBehavior.peekHeight = bottomSheetPeekHeight

                // Удаляем слушатель, чтобы предотвратить повторные вызовы
                shareButton.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setPlaylistImage(artworkUrl512: String) {
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .into(binding.playlistCover)
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.ic_placeholder_45x45)
            .fitCenter()
            .into(binding.extendedMenuPlaylistCover)
    }

    private fun setPlaylistName(name: String) {
        binding.playlistName.text = name
        binding.extendedMenuPlaylistName.text = name
    }

    private fun setTracksCount(count: Int) {
        binding.tracksCountPlaylist.text = TrackWordUtils(requireContext()).getTrackWord(count)
        binding.extendedMenuTracksCount.text = TrackWordUtils(requireContext()).getTrackWord(count)
    }

    private fun sharePlaylist() {
        if (playlist.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val shareText = playlistViewModel.buildShareText(playlist)
            playlistViewModel.sharePlaylist(shareText)
        }
    }

    private fun showContent() {
        setPlaylistImage(playlist.artworkUri)
        setPlaylistName(playlist.playlistName)
        setTracksCount(playlist.tracksCount)
        binding.playlistDescription.text = playlist.playlistDescription
        binding.playlistDuration.text = DurationUtils.getTotalMinutes(playlist.tracks)

        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(playlist.tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun refreshContent(playlist: Playlist) {
        setPlaylistImage(playlist.artworkUri)
        setPlaylistName(playlist.playlistName)
        binding.playlistDescription.text = playlist.playlistDescription
        binding.playlistDuration.text = DurationUtils.getTotalMinutes(playlist.tracks)
        binding.tracksCountPlaylist.text = TrackWordUtils(requireContext()).getTrackWord(playlist.tracksCount)

        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(playlist.tracks)
        trackListAdapter.notifyDataSetChanged()
        this.playlist = playlist
    }

    private fun showDeleteTrackConfirmationDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.alertStyle)
            .setTitle(requireContext().getString(R.string.delete_a_track))
            .setMessage("${
                requireContext().getString(R.string.do_you_really_want_to_delete_a_track)
            } \"${track.trackName}\"?")
            .setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(requireContext().getString(R.string.delete)) { dialog, _ ->
                deleteTrack(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistConfirmationDialog() {
        extendedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        MaterialAlertDialogBuilder(requireContext(), R.style.alertStyle)
            .setTitle(requireContext().getString(R.string.delete_a_track))
            .setMessage("${
                requireContext().getString(R.string.do_you_want_to_delete_a_playlist)
            } \"${playlist.playlistName}\"?")
            .setNegativeButton(requireContext().getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(requireContext().getString(R.string.yes)) { dialog, _ ->
                playlistViewModel.deletePlaylist(playlist)
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${requireContext().getString(R.string.playlist)}" +
                            " ${playlist.playlistName}" +
                            " ${requireContext().getString(R.string.deleted)}.",
                    Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun deleteTrack(track: Track) {
        playlistViewModel.deleteTrack(playlist.playlistId, track)
        val position = trackListAdapter.trackList.indexOf(track)
        if (position != -1) {
            trackListAdapter.trackList.removeAt(position)
            trackListAdapter.notifyItemRemoved(position)
        }
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