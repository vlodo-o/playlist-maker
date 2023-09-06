package com.practicum.playlistmaker.medialib.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FavoritesViewState {
    class FavoriteTracks(val tracks: List<Track>): FavoritesViewState()
    object Empty: FavoritesViewState()
}