package com.practicum.playlistmaker.medialib.ui.models

import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel

sealed class PlaylistsState {
    class Playlists(val playlists: List<PlaylistModel>): PlaylistsState()
    object Empty: PlaylistsState()
}