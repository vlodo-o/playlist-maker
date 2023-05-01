package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.player.domain.models.Track

interface PlayerView {
    fun drawTrack(track: Track)
    fun setPlayIcon()
    fun setPauseIcon()
    fun setDuration(ms: String)
}