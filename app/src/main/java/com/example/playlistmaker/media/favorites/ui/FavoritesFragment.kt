package com.example.playlistmaker.media.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.utils.gone
import com.example.playlistmaker.common.utils.show
import com.example.playlistmaker.common.ui.adapters.TrackListAdapter
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.favorites.ui.models.FavoritesFragmentState
import com.example.playlistmaker.player.ui.PlayerFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding

    private var isClickAllowed = true

    private val viewModel by viewModel<FavoritesViewModel>()
    private var adapter: TrackListAdapter? = null

    private lateinit var placeholder: LinearLayout
    private lateinit var favoriteList: RecyclerView
    private lateinit var trackList: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackListAdapter { item ->
            // Нажатие на итем
            if (clickDebounce()) {
                // Навигируемся на следующий экран
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playerFragment,
                    PlayerFragment.createArgs(item)
                )
            }
        }

        placeholder = binding.placeholder
        favoriteList = binding.favoritesList
        trackList = binding.trackList

        favoriteList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoriteList.adapter = adapter

        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        favoriteList.adapter = null
    }

    override fun onResume() {
        super.onResume()

        isClickAllowed = true
    }

    private fun render(state: FavoritesFragmentState) {
        when (state) {
            is FavoritesFragmentState.Content -> showContent(state.tracks)
            is FavoritesFragmentState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        placeholder.show()
        trackList.gone()
    }

    private fun showContent(tracks: List<Track>) {
        trackList.show()
        placeholder.gone()

        adapter?.trackList?.clear()
        adapter?.trackList?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

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
        private const val FAVORITES = "favorites"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance() = FavoritesFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}