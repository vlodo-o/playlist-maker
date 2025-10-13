package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.StateFlow

interface TrackPlayer {
    val playerStateFlow: StateFlow<PlayerState>
    fun playNewTrack(track: Track)
    fun start(trackUrl: String)
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)

}