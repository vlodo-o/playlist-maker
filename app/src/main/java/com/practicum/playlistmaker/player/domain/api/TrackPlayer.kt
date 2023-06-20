package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface TrackPlayer {
    var playerState: PlayerState
    fun prepare(trackUrl: String)
    fun start(trackUrl: String)
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
}