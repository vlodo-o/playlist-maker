package com.practicum.playlistmaker.medialib.ui.fragments.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.medialib.ui.models.FavoritesViewState
import com.practicum.playlistmaker.medialib.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.fragment.TrackListAdapter
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoriteTracksViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val trackListAdapter = TrackListAdapter (clickListener = { onTrackClickDebounce(it) })

    private lateinit var favoritesRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val bundle = bundleOf(PlayerActivity.TRACK to track)
            findNavController().navigate(R.id.action_medialibFragment_to_playerActivity, bundle)
        }

        favoritesRecyclerView = binding.favoritesRecyclerView

        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoritesRecyclerView.adapter = trackListAdapter

        viewModel.favoritesState.observe(viewLifecycleOwner) { state ->
            if (state is FavoritesViewState.FavoriteTracks) {
                trackListAdapter.setTracks(state.tracks)
                favoritesRecyclerView.visibility = View.VISIBLE
            }
            else {
                favoritesRecyclerView.visibility = View.GONE
            }
        }

        viewModel.getFavoriteTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoriteTracksFragment()

    }
}