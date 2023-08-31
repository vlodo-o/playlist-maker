package com.practicum.playlistmaker.main.ui.models

sealed class NavigationState {
    object SearchScreen: NavigationState()
    object MedialibScreen: NavigationState()
    object SettingsScreen: NavigationState()
}