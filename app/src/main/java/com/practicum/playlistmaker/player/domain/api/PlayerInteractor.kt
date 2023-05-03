package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface PlayerInteractor {
    fun preparePlayer()
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
    fun setTrackCompletionListener(listener: (() -> Unit))
}