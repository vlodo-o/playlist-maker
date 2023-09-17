package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.models.PlaylistsState
import com.practicum.playlistmaker.medialib.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()

    private lateinit var newPlayListButton: Button
    private lateinit var playlistsRecyclerView: RecyclerView

    private lateinit var onPlaylistClickDebounce: (PlaylistModel) -> Unit
    private val playlistAdapter = PlaylistAdapter { onPlaylistClickDebounce(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        onPlaylistClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            //val bundle = bundleOf(PlayerActivity.TRACK to track)
            //findNavController().navigate(R.id.action_medialibFragment_to_playerActivity, bundle)
        }

        playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsRecyclerView.adapter = playlistAdapter

        newPlayListButton.setOnClickListener {
            findNavController().navigate(R.id.action_medialibFragment_to_newPlaylistFragment)
        }

        viewModel.playlistsState.observe(viewLifecycleOwner) { state ->
            if (state is PlaylistsState.Playlists) {
                playlistAdapter.setPlaylists(state.playlists)
                playlistsRecyclerView.visibility = View.VISIBLE
            } else {
                playlistsRecyclerView.visibility = View.GONE
            }
        }

        viewModel.getPlaylists()
    }

    private fun initViews() {
        newPlayListButton = binding.newPlaylistButton
        playlistsRecyclerView = binding.playlistsRecyclerview
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = PlaylistsFragment()
    }
}