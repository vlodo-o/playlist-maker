package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewModel (private val interactor: PlayerInteractor): ViewModel() {

    private val _playState = MutableLiveData<Boolean>()
    val playState: LiveData<Boolean> = _playState

    private val _playProgress = MutableLiveData<String>()
    val playProgress: LiveData<String> = _playProgress

    private var timerJob: Job? = null

    private fun startPlayer(trackUrl: String) {
        interactor.startPlayer(trackUrl)
        _playState.value = true
        startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playState.value = false
        timerJob?.cancel()
    }

    fun stopPlayer() {
        interactor.stopPlayer()
        _playState.value = false
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
                    timerJob?.cancel()
                    _playProgress.value = TIMER_START
                }
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (interactor.getPlayerState() == PlayerState.PLAYING) {
                delay(DURATION_UPDATE_DELAY_MS)
                _playProgress.postValue(millisecondsToString(interactor.getCurrentPosition()))
            }
        }
    }

    private fun millisecondsToString(duration: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
    }

    companion object {
        const val TIMER_START = "00:00"
        private const val DURATION_UPDATE_DELAY_MS = 300L
    }

}