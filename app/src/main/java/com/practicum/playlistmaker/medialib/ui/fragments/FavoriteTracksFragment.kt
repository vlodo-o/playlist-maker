package com.practicum.playlistmaker.medialib.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialib.ui.view_model.FavoriteTracksViewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel by viewModels<FavoriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_tracks, container, false)
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}