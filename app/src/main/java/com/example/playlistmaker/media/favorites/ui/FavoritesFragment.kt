package com.example.playlistmaker.media.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FavoritesFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel { parametersOf(requireArguments().getString(FAVORITES)) }

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val FAVORITES = "favorites"

        fun newInstance() = FavoritesFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}