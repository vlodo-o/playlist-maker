package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistContentBinding
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.view_model.PlaylistContentViewModel
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.fragment.TrackListAdapter
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class PlaylistContentFragment : Fragment() {

    private var _binding: FragmentPlaylistContentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistContentViewModel>()

    private var onTrackClickDebounce: ((Track) -> Unit)? = null
    private val trackListAdapter = TrackListAdapter (
        clickListener = { onTrackClickDebounce?.let { it1 -> it1(it) } },
        longClickListener = { onLongClick(it) }
    )

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private var confirmDialog: MaterialAlertDialogBuilder? = null

    private lateinit var playlist: PlaylistModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentPlaylistContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = (arguments?.getSerializable(PLAYLIST_MODEL) as PlaylistModel)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { track ->
            val bundle = bundleOf(PlayerActivity.TRACK to track)
            findNavController().navigate(R.id.action_playlistContentFragment_to_playerActivity, bundle)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMoreBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        setPlaylistInfo(playlist)
        setListeners()

        binding.tracksRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.tracksRecyclerview.adapter = trackListAdapter
        viewModel.getAllPlaylistTracks(playlist.id)

        viewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            trackListAdapter.setTracks(tracks)
        }

        viewModel.tracksCount.observe(viewLifecycleOwner) { tracksCount ->
            binding.playlistTracksCount.text = tracksCount
            binding.playlistItem.tracksCountTextview.text = tracksCount
        }

        viewModel.playlistDuration.observe(viewLifecycleOwner) { duration ->
            binding.playlistDuration.text = duration
        }

        if (playlist.tracksCount == 0) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks),
                Toast.LENGTH_SHORT
            ).show()
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
        binding.playlistTracksCount.text = viewModel.getTrackCount(playlistModel.tracksCount)
        viewModel.sumTracksTime(playlistModel)

        Glide.with(requireContext())
            .load(file)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .into(binding.playlistItem.playlistCoverImageview)

        binding.playlistItem.playlistNameTextview.text = playlistModel.name
        binding.playlistItem.tracksCountTextview.text = viewModel.getTrackCount(playlistModel.tracksCount)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.buttonShare.setOnClickListener {
            sharePlaylist()
        }

        binding.moreButton.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.deletePlaylistButton.setOnClickListener {
            deletePLaylist()
        }

        binding.editInfoButton.setOnClickListener {
            val bundle = bundleOf(
                PLAYLIST_MODEL to playlist
            )
            findNavController().navigate(R.id.action_playlistContentFragment_to_editPlaylistFragment, bundle)
        }
    }

    fun onLongClick(track: Track) {
        confirmDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_track))
                .setMessage(getString(R.string.sure_delete_track))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.deleteTrackFromPlaylist(playlist, track.trackId!!)
                    }
                }.setNegativeButton(getString(R.string.no)) { _, _ -> }
        confirmDialog?.show()
    }

    fun sharePlaylist() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        if (!viewModel.sharePlaylist(playlist)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_track_to_share),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deletePLaylist() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        confirmDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_playlist)
                .setMessage(getString(R.string.want_delete_playlist))
                .setNegativeButton(R.string.no) { _, _ ->

                }.setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deletePlaylist(playlist)
                    findNavController().navigateUp()
                }
        confirmDialog?.show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        const val PLAYLIST_MODEL = "playlist"
        fun newInstance() = PlaylistContentFragment()
    }
}