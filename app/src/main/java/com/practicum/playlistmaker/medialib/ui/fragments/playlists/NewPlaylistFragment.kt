package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.medialib.ui.view_model.PlaylistsViewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PlaylistsViewModel>()

    private lateinit var coverImageView: ImageView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var createButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        coverImageView = binding.coverImageview
        nameEditText = binding.playlistName
        descriptionEditText = binding.playlistDescription
        createButton = binding.createPlaylistButton
    }

    private fun initListeners() {

        val textWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createButton.isEnabled = !nameEditText.text.isNullOrEmpty()
            }
            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        nameEditText.addTextChangedListener(textWatcher)

        createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }


    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}