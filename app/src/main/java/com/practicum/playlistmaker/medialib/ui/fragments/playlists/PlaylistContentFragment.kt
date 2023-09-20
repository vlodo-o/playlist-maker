package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Bundle
import android.os.Environment
import android.util.Log
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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
            val bundle = bundleOf(PlayerActivity.TRACK to track)
            findNavController().navigate(R.id.action_playlistContentFragment_to_playerActivity, bundle)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMoreBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        setPlaylistInfo(playlist)
        setListeners()

        binding.tracksRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
                .setTitle("Удалить трек")
                .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
                .setNegativeButton("Удалить") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.deleteTrackFromPlaylist(playlist, track.trackId!!)
                    }
                }.setPositiveButton(R.string.cancel) { _, _ -> }
        confirmDialog.show()
    }

    fun sharePlaylist() {
        if (!viewModel.sharePlaylist(playlist)) {
            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deletePLaylist() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        confirmDialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить плейлист")
                .setMessage("Хотите удалить плейлист?")
                .setNegativeButton("Нет") { _, _ ->

                }.setPositiveButton("Да") { _, _ ->
                    viewModel.deletePlaylist(playlist)
                    findNavController().navigateUp()
                }
        confirmDialog.show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PLAYLIST_MODEL = "playlist"
        fun newInstance() = PlaylistContentFragment()
    }
}