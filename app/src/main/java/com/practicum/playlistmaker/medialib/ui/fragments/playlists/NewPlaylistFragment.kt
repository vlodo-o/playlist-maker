package com.practicum.playlistmaker.medialib.ui.fragments.playlists

import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        val confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.create_fragment_close_title))
            .setMessage(requireContext().getString(R.string.create_fragment_close_mess))
            .setNegativeButton(requireContext().getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(requireContext().getString(R.string.finish)) { _, _ ->
                navigateBack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (imageUri == null &&
                    binding.playlistName.text.isNullOrEmpty() &&
                    binding.playlistDescription.text.isNullOrEmpty()) {
                    navigateBack()
                } else {
                    confirmDialog.show()
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            if (imageUri == null &&
                binding.playlistName.text.isNullOrEmpty() &&
                binding.playlistDescription.text.isNullOrEmpty()) {
                navigateBack()
            } else {
                confirmDialog.show()
            }
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
                if (nameEditText.text.isNullOrEmpty()) {
                    val color = "#" + Integer.toHexString(requireContext().getColor(R.color.yp_gray))
                    createButton.apply {
                        isEnabled = false
                        backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
                    }
                } else {
                    val color = "#" + Integer.toHexString(requireContext().getColor(R.color.yp_blue))
                    createButton.apply {
                        isEnabled = true
                        backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
                    }

                }
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
            val playlistName = nameEditText.text.toString()
            viewModel.createPlaylist(playlistName, descriptionEditText.text.toString(), imageUri.toString())
            val message = getString(R.string.playlist_created, playlistName)
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                findNavController().navigateUp()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateBack() {
        if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            findNavController().navigateUp()
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