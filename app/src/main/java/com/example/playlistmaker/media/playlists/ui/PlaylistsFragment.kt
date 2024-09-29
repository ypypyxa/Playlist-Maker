package com.example.playlistmaker.media.playlists.ui

import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import android.os.Bundle

class PlaylistsFragment : androidx.fragment.app.Fragment() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel { parametersOf(requireArguments().getString(
        PLAYLISTS
    )) }

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?) : android.view.View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val PLAYLISTS = "playlists"

        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}