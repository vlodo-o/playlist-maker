package com.practicum.playlistmaker.medialib.ui.view_model

import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : PlaylistsViewModel(playlistInteractor) {

    fun updatePlaylist(playlistModel: PlaylistModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (imageUri.value != null) {
                    playlistModel.imagePath = imageUri.value.toString()
            }
            playlistInteractor.updatePlaylist(playlistModel)
        }
    }

}