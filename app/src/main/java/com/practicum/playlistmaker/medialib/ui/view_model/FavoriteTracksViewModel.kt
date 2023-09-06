package com.practicum.playlistmaker.medialib.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialib.domain.MedialibInteractor
import com.practicum.playlistmaker.medialib.ui.models.FavoritesViewState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val medialibInteractor: MedialibInteractor
) : ViewModel() {

    private val _favoritesState = MutableLiveData<FavoritesViewState>()
    val favoritesState: LiveData<FavoritesViewState> = _favoritesState

    fun getFavoriteTracks() {
        viewModelScope.launch {
            medialibInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _favoritesState.postValue(FavoritesViewState.Empty)
                    }
                    else {
                        _favoritesState.postValue(FavoritesViewState.FavoriteTracks(tracks))
                    }
                }
        }
    }

}