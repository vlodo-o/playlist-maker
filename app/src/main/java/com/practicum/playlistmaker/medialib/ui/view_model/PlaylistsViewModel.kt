package com.practicum.playlistmaker.medialib.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.models.PlaylistsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val _playlistsState = MutableLiveData<PlaylistsState>()
    val playlistsState: LiveData<PlaylistsState> = _playlistsState

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri


    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    if (playlists.isNotEmpty()) {
                        _playlistsState.postValue(PlaylistsState.Playlists(playlists))
                    } else {
                        _playlistsState.postValue(PlaylistsState.Empty)
                    }
                }
        }
    }

    fun createPlaylist(name:String, description:String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.createPlaylist(name, description, imageUri.value.toString())
        }
    }

    fun saveImageToPrivateStorage(uri: Uri, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val privateUri = playlistInteractor.saveImageToPrivateStorage(uri, "cover_${System.currentTimeMillis()}.jpg")
            _imageUri.postValue(privateUri)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}