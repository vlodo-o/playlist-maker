package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(private val trackPlayer: TrackPlayer): PlayerInteractor {

    override val playerStateFlow = trackPlayer.playerStateFlow

    override fun playNewTrack(track: Track) {
        trackPlayer.playNewTrack(track)
    }

    override fun startPlayer(trackUrl: String) {
        trackPlayer.start(trackUrl)
    }

    override fun pausePlayer() {
        trackPlayer.pause()
    }

    override fun stopPlayer() {
        trackPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return trackPlayer.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return trackPlayer.playerStateFlow.value
    }

    override fun setTrackCompletionListener(listener: (() -> Unit)) {
        trackPlayer.setOnCompletionListener(listener)
    }

}