package com.practicum.playlistmaker.player.ui.models

sealed class NavigationState {
    object SearchScreen: NavigationState()
    object MedialibScreen: NavigationState()
    object SettingsScreen: NavigationState()
}