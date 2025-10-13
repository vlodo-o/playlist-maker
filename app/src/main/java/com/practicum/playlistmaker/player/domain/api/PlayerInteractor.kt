package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerInteractor {
    val playerStateFlow: StateFlow<PlayerState>
    fun playNewTrack(track: Track)
    fun startPlayer(trackUrl: String)
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
    fun setTrackCompletionListener(listener: (() -> Unit))
}