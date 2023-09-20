package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistContentBinding
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.view_model.PlaylistContentViewModel
import com.practicum.playlistmaker.medialib.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment
import com.practicum.playlistmaker.search.ui.fragment.TrackListAdapter
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class PlaylistContentFragment : Fragment() {

    private var _binding: FragmentPlaylistContentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistContentViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val trackListAdapter = TrackListAdapter (
        clickListener = { onTrackClickDebounce(it) },
        longClickListener = { onLongClick(it) }
    )

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private lateinit var playlist: PlaylistModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentPlaylistContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = (arguments?.getSerializable(PLAYLIST_MODEL) as PlaylistModel)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            Log.d("trackclick", "fffff")
            val bundle = bundleOf(PlayerActivity.TRACK to track)
            findNavController().navigate(R.id.action_playlistContentFragment_to_playerActivity, bundle)
        }

        setPlaylistInfo(playlist)
        setListeners()

        binding.tracksRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksRecyclerview.adapter = trackListAdapter
        viewModel.getAllPlaylistTracks(playlist.id)

        viewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            trackListAdapter.setTracks(tracks)
        }

    }

    private fun setPlaylistInfo(playlistModel: PlaylistModel) {
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_maker")
        val file = File(filePath, playlistModel.imagePath.toUri().lastPathSegment.toString())
        Glide.with(requireContext())
            .load(file)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .into(binding.playlistCover)

        binding.playlistName.text = playlistModel.name
        binding.playlistDescription.text = playlistModel.description
        binding.playlistTracksCount.text = playlistModel.tracksCount.toString()

        binding.playlistTracksCount.text = viewModel.sumTracksTime(playlistModel)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun onLongClick(track: Track) {
        Log.d("longtrackclick", "fffff")
        confirmDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить трек")
                .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
                .setNegativeButton("Удалить") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.deleteTrackFromPlaylist(playlist.id, track.trackId!!)
                    }
                }.setPositiveButton(R.string.cancel) { _, _ -> }
        confirmDialog.show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PLAYLIST_MODEL = "playlist"
        fun newInstance() = PlaylistContentFragment()
    }
}