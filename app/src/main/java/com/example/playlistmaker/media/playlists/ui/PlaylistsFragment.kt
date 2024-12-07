package com.example.playlistmaker.media.playlists.ui

import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Playlist
import com.example.playlistmaker.common.utils.gone
import com.example.playlistmaker.common.utils.show
import com.example.playlistmaker.media.playlist.ui.PlaylistFragment
import com.example.playlistmaker.media.playlists.ui.adapter.PlaylistsAdapter
import com.example.playlistmaker.media.playlists.ui.model.PlaylistsFragmentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistsFragment : Fragment() {

    private val playlistsViewModel by viewModel<PlaylistsViewModel>()

    private lateinit var binding: FragmentPlaylistsBinding

    private var isClickAllowed = true

    private lateinit var adapter: PlaylistsAdapter
    private lateinit var playlistsView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistsAdapter { item ->
            // Нажатие на итем
            if (clickDebounce()) {
                // Навигируемся на следующий экран
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playlistFragment,
                    PlaylistFragment.createArgs(item)
                )
            }
        }
        playlistsView = binding.playlistRecyclerView
        playlistsView.layoutManager = GridLayoutManager(requireContext(), /*Количество столбцов*/ 2) //ориентация по умолчанию — вертикальная
        playlistsView.adapter = adapter

        playlistsViewModel.fillData()

        playlistsViewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.createPlaylistButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        isClickAllowed = true
    }

    private fun render(state: PlaylistsFragmentState) {
        when (state) {
            is PlaylistsFragmentState.Content -> showContent(state.playlists)
            is PlaylistsFragmentState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.playlistIsEmptyPlaceholder.show()
        binding.playlistRecyclerView.gone()
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistRecyclerView.show()
        binding.playlistIsEmptyPlaceholder.gone()

        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
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

        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}