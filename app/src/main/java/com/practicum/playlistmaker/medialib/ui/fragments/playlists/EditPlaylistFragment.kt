package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment: NewPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()
    private lateinit var playlist: PlaylistModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        playlist = arguments?.getSerializable(PLAYLIST_MODEL) as PlaylistModel
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = "Редактировать"
        binding.createPlaylistButton.text = "Сохранить"
        binding.playlistName.setText(playlist.name)
        binding.playlistDescription.setText(playlist.description)

        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_maker")
        val file = File(filePath, playlist.imagePath.toUri().lastPathSegment.toString())

        Glide.with(requireContext())
            .load(file)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .into(binding.coverImageview)

        initListeners()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.createPlaylistButton.setOnClickListener {
            saveNewInfo()
            findNavController().navigateUp()
        }
    }

    override val onComplete: () -> Unit = {
        viewModel.updatePlaylist(playlist)
        findNavController().navigateUp()
    }


    fun saveNewInfo() {
        playlist.name = binding.playlistName.text.toString()
        playlist.description = binding.playlistDescription.text.toString()
        if (imageUri != null) {
            viewModel.saveImageToPrivateStorage(imageUri!!, onComplete)
            playlist.imagePath = imageUri.toString()
        } else {
            onComplete.invoke()
        }
    }

    companion object {
        const val PLAYLIST_MODEL = "playlist"
    }
}