package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.PlayTimer

class PlayerViewModel (private val interactor: PlayerInteractor): ViewModel() {

    private val playTimer: PlayTimer = PlayTimer(interactor) { _playProgress.postValue(it) }
    private val _playState = MutableLiveData<Boolean>()
    val playState: LiveData<Boolean> = _playState

    private val _playProgress = MutableLiveData<String>()
    val playProgress: LiveData<String> = _playProgress

    private fun startPlayer(trackUrl: String) {
        interactor.startPlayer(trackUrl)
        _playState.value = true
        playTimer.startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playState.value = false
        playTimer.pauseTimer()
    }

    fun stopPlayer() {
        interactor.stopPlayer()

    }

    fun playbackControl(trackUrl: String) {
        when(interactor.getPlayerState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer(trackUrl)
            }
            PlayerState.DEFAULT -> {
                startPlayer(trackUrl)
                interactor.setTrackCompletionListener {
                    _playState.value = false
                    playTimer.pauseTimer()
                    _playProgress.value = TIMER_START
                }
            }
        }
    }

    companion object {
        const val TIMER_START = "00:00"
    }

}