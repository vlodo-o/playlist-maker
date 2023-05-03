package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState

class TrackPlayerImpl (private val trackUrl: String): TrackPlayer {

    override var playerState = PlayerState.DEFAULT
    private val mediaPlayer = MediaPlayer()
    override var completionListener: () -> Unit = { }

    override fun prepare() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            completionListener.invoke()
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }


}