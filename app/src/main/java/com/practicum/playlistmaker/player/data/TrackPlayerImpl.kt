package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.player.domain.models.PlayerState

class TrackPlayerImpl: TrackPlayer {

    override var playerState = PlayerState.DEFAULT
    private var mediaPlayer: MediaPlayer? = null

    override fun prepare(trackUrl: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepare()
        playerState = PlayerState.PREPARED
    }

    override fun start(trackUrl: String) {
        if (playerState == PlayerState.DEFAULT) {
            prepare(trackUrl)
        }
        mediaPlayer?.start()
        playerState = PlayerState.PLAYING
    }

    override fun pause() {
        if (playerState != PlayerState.DEFAULT) {
            mediaPlayer?.pause()
            playerState = PlayerState.PAUSED
        }
    }

    override fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        playerState = PlayerState.DEFAULT
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer?.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            listener.invoke()
        }
    }


}