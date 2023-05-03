package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState

class PlayerInteractorImpl(private val trackPlayer: TrackPlayer): PlayerInteractor {

    override fun preparePlayer() {
        trackPlayer.prepare()
    }
    override fun startPlayer() {
        trackPlayer.start()
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
        return trackPlayer.playerState
    }

    override fun setTrackCompletionListener(listener: (() -> Unit)) {
        trackPlayer.completionListener = listener
    }

}