package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface TrackPlayer {
    var playerState: PlayerState
    var completionListener: () -> Unit
    fun prepare()
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}