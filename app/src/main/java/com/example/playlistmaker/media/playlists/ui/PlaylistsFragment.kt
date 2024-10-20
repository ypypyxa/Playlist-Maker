package com.example.playlistmaker.media.playlists.ui

import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.example.playlistmaker.R

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
        }
    }

    companion object {
        private const val PLAYLISTS = "playlists"

        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}