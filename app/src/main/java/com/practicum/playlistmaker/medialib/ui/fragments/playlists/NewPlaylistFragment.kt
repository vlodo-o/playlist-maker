package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.medialib.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()

    private lateinit var coverImageView: ImageView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var createButton: Button

    private var imageUri: Uri? = null
    private var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback вызовется, когда пользователь выберет картинку
        if (uri != null) {
            Log.d("PhotoPicker", "Выбранный URI: $uri")
            coverImageView.setImageURI(uri)
            viewModel.saveImageToPrivateStorage(uri)
        } else {
            Log.d("PhotoPicker", "Ничего не выбрано")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()

        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            imageUri = uri
        }
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
        
        coverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        createButton.setOnClickListener {
            viewModel.createPlaylist(nameEditText.text.toString(), descriptionEditText.text.toString(), imageUri.toString())
            findNavController().navigateUp()
            val message = resources.getString(R.string.playlist_created)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}