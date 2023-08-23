package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.main.ui.models.NavigationState

class MainViewModel: ViewModel() {

    private val _navigationLiveData = MutableLiveData<NavigationState>()
    val navigationLiveData: LiveData<NavigationState> = _navigationLiveData

    fun openSearchScreen() {
        _navigationLiveData.value = NavigationState.SearchScreen
    }

    fun openMedialibScreen() {
        _navigationLiveData.value = NavigationState.MedialibScreen
    }

    fun openSettingsScreen() {
        _navigationLiveData.value = NavigationState.SettingsScreen
    }

}