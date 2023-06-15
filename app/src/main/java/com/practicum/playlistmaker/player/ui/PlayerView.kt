package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerView {
    fun drawTrack(track: Track)
    fun setPlayIcon()
    fun setPauseIcon()
    fun setDuration(ms: String)
}