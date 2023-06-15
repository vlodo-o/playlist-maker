package com.practicum.playlistmaker.main.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.ui.models.NavigationState
import com.practicum.playlistmaker.search.ui.models.SearchViewState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

class MainViewModel: ViewModel() {

    private val _navigationLiveData = MutableLiveData<NavigationState>()
    val navigationLiveData: LiveData<NavigationState> = _navigationLiveData


    fun openSearchScreen() {
        _navigationLiveData.postValue(NavigationState.SearchScreen)
    }

    fun openMedialibScreen() {
        _navigationLiveData.postValue(NavigationState.MedialibScreen)
    }

    fun openSettingsScreen() {
        _navigationLiveData.postValue(NavigationState.SettingsScreen)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    MainViewModel(
                    )

                }
            }
    }

}